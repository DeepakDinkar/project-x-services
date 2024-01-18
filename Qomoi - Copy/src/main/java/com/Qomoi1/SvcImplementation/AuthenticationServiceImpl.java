package com.Qomoi1.SvcImplementation;


import com.Qomoi1.Entity.UserEntity;
import com.Qomoi1.Enum.Role;
import com.Qomoi1.Repository.UserRepository;
import com.Qomoi1.Request.SigninRequest;
import com.Qomoi1.Request.SignupRequest;
import com.Qomoi1.Response.JWTAuthenticationResponse;
import com.Qomoi1.Service.AuthenticationService;
import com.Qomoi1.Service.JWTService;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private  UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    private  AuthenticationManager authenticationManager;

    private final  JWTService jwtService;

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

}
