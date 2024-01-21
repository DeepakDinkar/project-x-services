package com.Qomoi1.Controller;



import com.Qomoi1.Entity.UserEntity;
import com.Qomoi1.Exception.MissingFieldException;
import com.Qomoi1.Exception.NotFoundException;
import com.Qomoi1.Repository.UserRepository;
import com.Qomoi1.Request.ForgetPasswordDto;
import com.Qomoi1.Request.SigninRequest;
import com.Qomoi1.Request.SignupRequest;
import com.Qomoi1.Response.JWTAuthenticationResponse;
import com.Qomoi1.Response.ResponseDto;
import com.Qomoi1.Response.SignupResponse;
import com.Qomoi1.Service.AuthenticationService;
import com.Qomoi1.Service.UserService;
import com.Qomoi1.Utility.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private  AuthenticationService authenticationService;

    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Value("${front.end}")
    private String frontEndUrl;



    @PostMapping("/user")
    public ResponseEntity<SignupResponse> signupUser(@RequestBody SignupRequest signupRequest) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(signupRequest.getEmail());
        if (existingUser.isPresent()) {
            String message = "User with email " + signupRequest.getEmail() + " already exists";
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new SignupResponse(message, null));
        } else {
            UserEntity newUser = authenticationService.signupUser(signupRequest);
            String message = "Successfully registered";
            SignupResponse response = new SignupResponse(message, newUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<SignupResponse> signupAdmin(@RequestBody SignupRequest signupRequest){
        Optional<UserEntity> existingUser = userRepository.findByEmail(signupRequest.getEmail());
        if (existingUser.isPresent()) {
            String message = "Admin with email " + signupRequest.getEmail() + " already exists";
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new SignupResponse(message, null));
        } else {
            UserEntity newUser = authenticationService.signupAdmin(signupRequest);
            String message = "Successfully registered";
            SignupResponse response = new SignupResponse(message, newUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PostMapping("/signIn")
    public ResponseEntity<JWTAuthenticationResponse> signIn(@RequestBody SigninRequest signinRequest){
        return ResponseEntity.ok(authenticationService.signin(signinRequest));
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> processForgotPassword(@RequestBody ForgetPasswordDto forgetPasswordDto, HttpServletRequest request, Model model) throws MissingFieldException, NotFoundException, JsonProcessingException, NotFoundException {

        String email = forgetPasswordDto.getEmailId();
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        if (!StringUtils.hasText(email)) {
            throw new MissingFieldException(Constants.EMAIL_ID_MANDATORY);
        }
        authenticationService.updateResetPasswordToken(token, email);

        try {

            String resetPasswordLink = frontEndUrl + "/reset-password?token=" + token;
            String subject = "Here's the link to reset your password";

            String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
                    + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + resetPasswordLink
                    + "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password "
                    + "or you have not made the request.</p>";
            authenticationService.sendEmail(email, subject, content);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
        } catch (UnsupportedEncodingException | MessagingException e) {
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

        UserEntity user = authenticationService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return ResponseEntity
                    .status(401)
                    .body(new ResponseDto(5, Constants.UNAUTHORIZED));
        } else {
            authenticationService.updatePassword(user, password);
            model.addAttribute("message", "You have successfully changed your password.");
        }

        return ResponseEntity.ok().body(new ResponseDto(200, Constants.PASSWORD_UPDATED_SUCCESSFULLY));
    }


}
