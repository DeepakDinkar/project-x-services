package com.Qomoi.Qomoi.Controller;


import com.Qomoi.Qomoi.Entity.UserEntity;
import com.Qomoi.Qomoi.Request.SignupRequest;
import com.Qomoi.Qomoi.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/user")
    public ResponseEntity<UserEntity> signupUser(@RequestBody SignupRequest signupRequest){
        return ResponseEntity.ok(authenticationService.signupUser(signupRequest));
    }

    @PostMapping("/admin")
    public ResponseEntity<UserEntity> signupAdmin(@RequestBody SignupRequest signupRequest){
        return ResponseEntity.ok(authenticationService.signupAdmin(signupRequest));
    }


}
