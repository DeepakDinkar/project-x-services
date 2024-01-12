package com.Qomoi1.SvcImplementation;


import com.Qomoi1.Entity.UserEntity;
import com.Qomoi1.Enum.Role;
import com.Qomoi1.Repository.UserRepository;
import com.Qomoi1.Request.SigninRequest;
import com.Qomoi1.Request.SignupRequest;
import com.Qomoi1.Response.JWTAuthenticationResponse;
import com.Qomoi1.Service.AuthenticationService;
import com.Qomoi1.Service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    public UserEntity signupUser(SignupRequest signupRequest ){

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setFirstName(signupRequest.getFirstName());
        userEntity.setLastName(signupRequest.getLastName());
        userEntity.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
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
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),signinRequest.getPassword()));

        var user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(()-> new IllegalArgumentException("Invalid mail or password"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JWTAuthenticationResponse jwtAuthenticationResponse = new JWTAuthenticationResponse();
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        return jwtAuthenticationResponse;

    }

}
