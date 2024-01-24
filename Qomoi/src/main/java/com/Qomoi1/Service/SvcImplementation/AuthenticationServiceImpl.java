package com.Qomoi1.Service.SvcImplementation;


import com.Qomoi1.Entity.UserEntity;
import com.Qomoi1.Enum.Role;
import com.Qomoi1.Exception.NotFoundException;
import com.Qomoi1.Repository.UserRepository;
import com.Qomoi1.Request.SigninRequest;
import com.Qomoi1.Request.SignupRequest;
import com.Qomoi1.Response.JWTAuthenticationResponse;
import com.Qomoi1.Service.AuthenticationService;
import com.Qomoi1.Service.JWTService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private  UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserEntity signupUser(SignupRequest signupRequest ){

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setFirstName(signupRequest.getFirstName());
        userEntity.setLastName(signupRequest.getLastName());
        userEntity.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        System.out.println(passwordEncoder.matches(signupRequest.getPassword(),userEntity.getPassword()));
        userEntity.setRole(Role.USER);

        userRepository.save(userEntity);
        return userEntity;
    }

    public UserEntity signupAdmin(SignupRequest signupRequest ){

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setFirstName(signupRequest.getFirstName());
        userEntity.setLastName(signupRequest.getLastName());
        userEntity.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        userEntity.setRole(Role.ADMIN);

        userRepository.save(userEntity);
        return userEntity;
    }

    public JWTAuthenticationResponse signin(SigninRequest signinRequest){
        System.out.println(userRepository.findByEmail(signinRequest.getEmail()));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),signinRequest.getPassword()));
        System.out.println(userRepository.findByEmail(signinRequest.getEmail()));
        var user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(()-> new IllegalArgumentException("Invalid mail or password"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JWTAuthenticationResponse jwtAuthenticationResponse = new JWTAuthenticationResponse();
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        return jwtAuthenticationResponse;

    }

    public void updateResetPasswordToken(String token, String email) throws NotFoundException {
        Optional<UserEntity> userVal = userRepository.findByEmail(email);
        if (userVal.isPresent()) {
          UserEntity user = userVal.get();
          user.setUserId(user.getUserId());
          user.setResetPasswordToken(token);
          userRepository.save(user);
        } else {
            throw new NotFoundException("Email is not registered with PV: " + email);
        }
    }

    public void sendEmail(String recipientEmail, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("support@qomol.com", "Qomol Support");
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public UserEntity getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(UserEntity userVal, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);

        userVal.setPassword(encodedPassword);
        userVal.setResetPasswordToken(null);

        userRepository.save(userVal);
    }
}
