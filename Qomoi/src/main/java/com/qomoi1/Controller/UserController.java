package com.qomoi1.Controller;


import com.qomoi1.Repository.UserRepository;
import com.qomoi1.Service.impl.RefreshTokenServiceImpl;
import com.qomoi1.Service.impl.UserDetailsImpl;
import com.qomoi1.Service.impl.UserServiceImpl;
import com.qomoi1.Utility.Constants;
import com.qomoi1.Utility.Decrypt;
import com.qomoi1.dto.*;
import com.qomoi1.entity.RefreshToken;
import com.qomoi1.entity.UserDE;
import com.qomoi1.exception.ExistingUserFoundException;
import com.qomoi1.exception.MissingFieldException;
import com.qomoi1.exception.NotFoundException;
import com.qomoi1.exception.TokenRefreshException;
import com.qomoi1.jwt.JwtUtils;
import com.qomoi1.validator.ValidateUserFields;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenServiceImpl refreshTokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${front.end}")
    private String frontEndUrl;

    @PostMapping("/signup")
    public ResponseEntity<?> saveUser(@RequestBody SignUpRequestDTO signUpRequestDTO)
            throws MissingFieldException, ExistingUserFoundException {

        try{
            ValidateUserFields validateUserFields = new ValidateUserFields();
            validateUserFields.validateSignUpFields(signUpRequestDTO);
            UserDE userRegistered = null;

            if(userService.getByEmailIdAndMobileNumber(signUpRequestDTO.getEmailId(), signUpRequestDTO.getMobile())==null){
                userRegistered = userService.saveUser(signUpRequestDTO);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new SavedRecordResponseDto(userRegistered, new ResponseDto(201, "Record saved successfully")));
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
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmailId(), decryptPass.decrypt(loginRequestDTO.getPassword())));

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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws NotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
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

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
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
    public ResponseEntity<String> googleSignup( @RequestBody GoogleSigninRequest googleSigninRequest) throws GeneralSecurityException, IOException {

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
                .setSubject(googleTokenResponse.getSub())
                .claim("name", googleTokenResponse.getName())
                .claim("email", googleTokenResponse.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .signWith(SignatureAlgorithm.HS256, keyBytes)
                .compact();
        String responseBody = response.getBody();
        String extractedBody = responseBody.substring(responseBody.indexOf("{"), responseBody.lastIndexOf("}") + 1);
        return new ResponseEntity<>(extractedBody + "\nJWT Token: " + jwtToken + "\n" + user, HttpStatus.OK);
    }

    @PostMapping("/save-address/id")
    public ResponseEntity<String> saveAddress(@RequestBody AddressDto addressDto, @PathVariable Long id) {
        if (id != null && addressDto != null) {
            userService.saveAddress(addressDto, id);
            return ResponseEntity.ok("Address saved Successfully");
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @PostMapping("/forgot_password")
//    public ResponseEntity<?> processForgotPassword(@RequestBody ForgetPasswordDto forgetPasswordDto, HttpServletRequest request, Model model) throws MissingFieldException, NotFoundException, JsonProcessingException, NotFoundException {
//
//        String email = forgetPasswordDto.getEmailId();
//        String token = UUID.randomUUID().toString().replaceAll("-", "");
//        if (!StringUtils.hasText(email)) {
//            throw new MissingFieldException(Constants.EMAIL_ID_MANDATORY);
//        }
//        userService.updateResetPasswordToken(token, email);
//
//        try {
//
//            String resetPasswordLink = frontEndUrl + "/reset-password?token=" + token;
//            String subject = "Here's the link to reset your password";
//
//            String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
//                    + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + resetPasswordLink
//                    + "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password "
//                    + "or you have not made the request.</p>";
//            userService.sendEmail(email, subject, content);
//            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
//        } catch (UnsupportedEncodingException | MessagingException e) {
//            model.addAttribute("error", "Error while sending email");
//        }
//        return ResponseEntity.ok().body(new ResponseDto(200, Constants.MAIL_SENT_SUCCESSFULLY));
//    }
//
//    @PostMapping("/reset_password")
//    public ResponseEntity<?> processResetPassword( HttpServletRequest request, Model model) throws MissingFieldException, JsonProcessingException {
//
//        String token = request.getParameter("token");
//        String password = request.getParameter("password");
//        if (!StringUtils.hasText(token)) {
//            throw new MissingFieldException(Constants.TOKEN_MANDATORY);
//        }
//        if (!StringUtils.hasText(password)) {
//            throw new MissingFieldException(Constants.PASSWORD_MANDATORY);
//        }
//
//        UserDE user = userService.getByResetPasswordToken(token);
//        model.addAttribute("title", "Reset your password");
//
//        if (user == null) {
//            model.addAttribute("message", "Invalid Token");
//            return ResponseEntity
//                    .status(401)
//                    .body(new ResponseDto(5, Constants.UNAUTHORIZED));
//        } else {
//            userService.updatePassword(user, password);
//            model.addAttribute("message", "You have successfully changed your password.");
//        }
//
//        return ResponseEntity.ok().body(new ResponseDto(200, Constants.PASSWORD_UPDATED_SUCCESSFULLY));
//    }

//    public void TrippleDes() throws Exception {
//        myEncryptionKey = "ThisIsSpartaThisIsSparta";
//        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
//        arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
//        ks = new DESedeKeySpec(arrayBytes);
//        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
//        cipher = Cipher.getInstance(myEncryptionScheme);
//        key = skf.generateSecret(ks);
//    }
//
//
//    public String decrypt(String encryptedString) {
//        String decryptedText=null;
//        try {
//            cipher.init(Cipher.DECRYPT_MODE, key);
//            byte[] encryptedText = Base64.decodeBase64(encryptedString);
//            byte[] plainText = cipher.doFinal(encryptedText);
//            decryptedText= new String(plainText);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return decryptedText;
//    }




}
