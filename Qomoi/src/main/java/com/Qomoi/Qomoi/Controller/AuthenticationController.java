package com.Qomoi.Qomoi.Controller;


import com.Qomoi.Qomoi.Entity.UserEntity;
import com.Qomoi.Qomoi.Request.SigninRequest;
import com.Qomoi.Qomoi.Request.SignupRequest;
import com.Qomoi.Qomoi.Response.JWTAuthenticationResponse;
import com.Qomoi.Qomoi.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final AuthenticationManager authenticationManager;


    @PostMapping("/user")
    public ResponseEntity<UserEntity> signupUser(@RequestBody SignupRequest signupRequest){
        return ResponseEntity.ok(authenticationService.signupUser(signupRequest));
    }

    @PostMapping("/admin")
    public ResponseEntity<UserEntity> signupAdmin(@RequestBody SignupRequest signupRequest){
        return ResponseEntity.ok(authenticationService.signupAdmin(signupRequest));
    }

    @PostMapping("/signIn")
    public ResponseEntity<JWTAuthenticationResponse> signIn(@RequestBody SigninRequest signinRequest){
        return ResponseEntity.ok(authenticationService.signin(signinRequest));
    }


}
