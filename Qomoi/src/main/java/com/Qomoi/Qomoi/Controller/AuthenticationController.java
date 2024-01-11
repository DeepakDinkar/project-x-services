package com.Qomoi.Qomoi.Controller;


import com.Qomoi.Qomoi.Entity.UserEntity;
import com.Qomoi.Qomoi.Repository.UserRepository;
import com.Qomoi.Qomoi.Request.SigninRequest;
import com.Qomoi.Qomoi.Request.SignupRequest;
import com.Qomoi.Qomoi.Response.JWTAuthenticationResponse;
import com.Qomoi.Qomoi.Response.SignupResponse;
import com.Qomoi.Qomoi.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

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


}
