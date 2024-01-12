package com.Qomoi1.Controller;



import com.Qomoi1.Entity.UserEntity;
import com.Qomoi1.Repository.UserRepository;
import com.Qomoi1.Request.SigninRequest;
import com.Qomoi1.Request.SignupRequest;
import com.Qomoi1.Response.JWTAuthenticationResponse;
import com.Qomoi1.Response.SignupResponse;
import com.Qomoi1.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
