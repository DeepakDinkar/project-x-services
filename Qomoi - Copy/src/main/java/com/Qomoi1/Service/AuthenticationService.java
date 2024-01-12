package com.Qomoi1.Service;
import com.Qomoi1.Entity.UserEntity;
import com.Qomoi1.Request.SigninRequest;
import com.Qomoi1.Request.SignupRequest;
import com.Qomoi1.Response.JWTAuthenticationResponse;

public interface AuthenticationService {

    UserEntity signupUser(SignupRequest signupRequest );
    UserEntity signupAdmin(SignupRequest signupRequest );

    JWTAuthenticationResponse signin (SigninRequest signinRequest);
}
