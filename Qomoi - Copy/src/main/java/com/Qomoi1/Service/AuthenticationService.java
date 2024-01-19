package com.Qomoi1.Service;
import com.Qomoi1.Entity.UserEntity;
import com.Qomoi1.Exception.NotFoundException;
import com.Qomoi1.Request.SigninRequest;
import com.Qomoi1.Request.SignupRequest;
import com.Qomoi1.Response.JWTAuthenticationResponse;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface AuthenticationService {

    UserEntity signupUser(SignupRequest signupRequest );
    UserEntity signupAdmin(SignupRequest signupRequest );

    JWTAuthenticationResponse signin (SigninRequest signinRequest);

    void updateResetPasswordToken(String token, String email) throws NotFoundException;

    public void sendEmail(String recipientEmail, String subject, String content)
            throws MessagingException, UnsupportedEncodingException;

    UserEntity getByResetPasswordToken(String token);

    void updatePassword(UserEntity userVal, String newPassword);

}
