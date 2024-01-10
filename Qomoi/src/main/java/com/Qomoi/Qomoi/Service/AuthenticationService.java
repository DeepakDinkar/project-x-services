package com.Qomoi.Qomoi.Service;

import com.Qomoi.Qomoi.Entity.UserEntity;
import com.Qomoi.Qomoi.Request.SigninRequest;
import com.Qomoi.Qomoi.Request.SignupRequest;
import com.Qomoi.Qomoi.Response.JWTAuthenticationResponse;

public interface AuthenticationService {

    UserEntity signupUser(SignupRequest signupRequest );
    UserEntity signupAdmin(SignupRequest signupRequest );

    JWTAuthenticationResponse signin (SigninRequest signinRequest);
}
