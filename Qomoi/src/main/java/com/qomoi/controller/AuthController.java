package com.qomoi.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qomoi.dto.*;
import com.qomoi.entity.RefreshToken;
import com.qomoi.entity.UserDE;
import com.qomoi.exception.ExistingUserFoundException;
import com.qomoi.exception.MissingFieldException;
import com.qomoi.exception.NotFoundException;
import com.qomoi.exception.TokenRefreshException;
import com.qomoi.jwt.JwtUtils;
import com.qomoi.repository.UserRepository;
import com.qomoi.service.impl.RefreshTokenServiceImpl;
import com.qomoi.service.impl.UserDetailsImpl;
import com.qomoi.service.impl.UserServiceImpl;
import com.qomoi.utility.Constants;
import com.qomoi.utility.Decrypt;
import com.qomoi.validator.ValidateUserFields;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${pv.app.jwtSecret}")
    private String jwtSecret;
    @Value("${front.end}")
    private String frontEndUrl;

    public AuthController(UserServiceImpl userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenServiceImpl refreshTokenService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> saveUser(@RequestBody SignUpRequestDTO signUpRequestDTO)
            throws MissingFieldException, ExistingUserFoundException {

        try{
            ValidateUserFields validateUserFields = new ValidateUserFields();
            validateUserFields.validateSignUpFields(signUpRequestDTO);
            UserDE userRegistered = null;

            if(userService.getByEmailIdAndMobileNumber(signUpRequestDTO.getEmailId(), signUpRequestDTO.getMobile())==null){
                userRegistered = userService.saveUser(signUpRequestDTO);
                SignupResponseDto signupResponseDto = new SignupResponseDto();
                signupResponseDto.setFirstName(userRegistered.getFirstName());
                signupResponseDto.setLastName(userRegistered.getLastName());
                signupResponseDto.setEmail(userRegistered.getEmailId());
                signupResponseDto.setMobile(userRegistered.getMobile());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new SavedResponseDto(signupResponseDto, new ResponseDto(201, "Record saved successfully")));
            }
            else{
                throw new ExistingUserFoundException("User already exists");
            }
        }
        catch(ExistingUserFoundException e){
            throw new ExistingUserFoundException(e.getMessage());
        }
        catch (MissingFieldException e){
            throw new MissingFieldException("Fields missing");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) throws Exception {
        Decrypt decryptPass = new Decrypt();

        String salt = userRepository.findSaltByEmailId(loginRequestDTO.getEmailId());
        String password = decryptPass.decrypt(loginRequestDTO.getPassword());

        String saltPass = salt+password;

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmailId(), saltPass ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new JwtResponse(jwtCookie.getValue()));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle.toString() != "anonymousUser") {
            Long userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId);
        }

        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new ResponseDto(200, Constants.SIGNOUT_SUCCESSFULLY));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new ResponseDto(200, Constants.TOKEN_REFRESHED_SUCCESSFULLY));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            Constants.TOKEN_REFRESHED_NOT_AVAILABLE));
        }

        return ResponseEntity.badRequest().body(new ResponseDto(4, Constants.TOKEN_EMPTY));
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleSignup( @RequestBody GoogleSigninRequest googleSigninRequest) throws GeneralSecurityException, IOException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + googleSigninRequest.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        GoogleTokenResponse googleTokenResponse = objectMapper.readValue(response.getBody(), GoogleTokenResponse.class);

        String user = userService.saveGoogleLogin(googleTokenResponse);

        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
        String jwtToken = Jwts.builder()
                .setSubject(googleTokenResponse.getEmail())
                .claim("name", googleTokenResponse.getName())
                .claim("email", googleTokenResponse.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
//        String responseBody = response.getBody();
//        String extractedBody = responseBody.substring(responseBody.indexOf("{"), responseBody.lastIndexOf("}") + 1);

        GoogleResponse googleResponse = new GoogleResponse();
        googleResponse.setToken(jwtToken);
        googleResponse.setUser(user);
        googleResponse.setAdditionalInfo(response.getBody());

        return new ResponseEntity<>(googleResponse, HttpStatus.OK);
    }

    private Key getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> processForgotPassword(@RequestBody ForgetPasswordDto forgetPasswordDto, HttpServletRequest request, Model model) throws MissingFieldException, NotFoundException, JsonProcessingException, NotFoundException {

        String email = forgetPasswordDto.getEmailId();
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        if (!StringUtils.hasText(email)) {
            throw new MissingFieldException(Constants.EMAIL_ID_MANDATORY);
        }
        userService.updateResetPasswordToken(token, email);
        try {
            String resetPasswordLink = frontEndUrl + "/reset-password?token=" + token;
            String subject = "Here's the link to reset your password";

            String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
                    + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + resetPasswordLink
                    + "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password "
                    + "or you have not made the request.</p>";
            userService.sendEmail(email, subject, content);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", "Error while sending email");
        }
        return ResponseEntity.ok().body(new ResponseDto(200, Constants.MAIL_SENT_SUCCESSFULLY));
    }

    @PostMapping("/reset_password")
    public ResponseEntity<?> processResetPassword( HttpServletRequest request, Model model) throws MissingFieldException, JsonProcessingException {

        String token = request.getParameter("token");
        String password = request.getParameter("password");
        if (!StringUtils.hasText(token)) {
            throw new MissingFieldException(Constants.TOKEN_MANDATORY);
        }
        if (!StringUtils.hasText(password)) {
            throw new MissingFieldException(Constants.PASSWORD_MANDATORY);
        }
        UserDE customer = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");
        if (customer == null) {
            model.addAttribute("message", "Invalid Token");
            return ResponseEntity
                    .status(401)
                    .body(new ResponseDto(5, Constants.UNAUTHORIZED));
        } else {
            userService.updatePassword(customer, password);
            model.addAttribute("message", "You have successfully changed your password.");
        }

        return ResponseEntity.ok().body(new ResponseDto(200, Constants.PASSWORD_UPDATED_SUCCESSFULLY));
    }



}
