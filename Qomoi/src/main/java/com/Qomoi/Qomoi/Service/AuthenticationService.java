package com.Qomoi.Qomoi.Service;

import com.Qomoi.Qomoi.Entity.UserEntity;
import com.Qomoi.Qomoi.Request.SignupRequest;

public interface AuthenticationService {

     UserEntity signupUser(SignupRequest signupRequest);

     UserEntity signupAdmin(SignupRequest signupRequest);
}
