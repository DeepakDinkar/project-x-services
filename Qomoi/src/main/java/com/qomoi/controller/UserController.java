package com.qomoi.controller;


import com.qomoi.repository.UserRepository;
import com.qomoi.service.impl.RefreshTokenServiceImpl;
import com.qomoi.service.impl.UserDetailsImpl;
import com.qomoi.service.impl.UserServiceImpl;
import com.qomoi.utility.Constants;
import com.qomoi.utility.Decrypt;
import com.qomoi.dto.*;
import com.qomoi.entity.RefreshToken;
import com.qomoi.entity.UserDE;
import com.qomoi.exception.ExistingUserFoundException;
import com.qomoi.exception.MissingFieldException;
import com.qomoi.exception.NotFoundException;
import com.qomoi.exception.TokenRefreshException;
import com.qomoi.jwt.JwtUtils;
import com.qomoi.validator.ValidateUserFields;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

@RestController
    @RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;
//    private final AuthenticationManager authenticationManager;
//    private final JwtUtils jwtUtils;
//    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserRepository userRepository;

//    private final PasswordEncoder passwordEncoder;

    @Value("${front.end}")
    private String frontEndUrl;

    public UserController(UserServiceImpl userService,  UserRepository userRepository) {
        this.userService = userService;
//        this.authenticationManager = authenticationManager;
//        this.jwtUtils = jwtUtils;
//        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws NotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/saveProfile/{email}")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileDto profileDto, @PathVariable String email){
        if(StringUtils.hasText(email)){
          UserDE userDE = userService.updateProfile(profileDto,email);
          return ResponseEntity.status(HttpStatus.CREATED)
                  .body(new SavedRecordResponseDto(userDE,new ResponseDto(201, "Record saved successfully")));
        }
        throw new EntityNotFoundException("User with email " + email + " not found");
    }

    @GetMapping("/myProfile/{email}")
    public ResponseEntity<?> getProfile(@PathVariable String email){
        if(StringUtils.hasText(email)){
            UserDE userDE = userService.getProfile(email);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new SavedRecordResponseDto(userDE,new ResponseDto(200, "User found")));
        }
        throw new EntityNotFoundException("User with email " + email + " not found");
    }


    @PostMapping("/save-address/{id}")
    public ResponseEntity<String> saveAddress(@RequestBody AddressDto addressDto, @PathVariable Long id) {
        if (id != null && addressDto != null) {
            userService.saveAddress(addressDto, id);
            return ResponseEntity.ok("Address saved Successfully");
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @PostMapping("/myProfile/{email}")
//    public ResponseEntity<UserProfileResponse> getMyProfile(@PathVariable String email){
//        return null;
//    }

    @PostMapping("/myPurchase/{email}")
    public ResponseEntity<PurchaseResponse> getPurchaseInfo(@PathVariable String email){
        return null;
    }

    @PostMapping("/myCourses/{email}")
    public ResponseEntity<CourseResponse> getMyCourses(@PathVariable String email){
        return null;
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


}
