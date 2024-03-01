package com.qomoi.controller;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

import ch.qos.logback.core.util.SystemInfo;
import com.qomoi.entity.AddToCart;
import com.qomoi.modal.KeyList;
import com.qomoi.repository.AddToCartRepository;
import com.qomoi.service.EncryptDecryptKey;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qomoi.dto.ForgetPasswordDto;
import com.qomoi.dto.GoogleSigninRequest;
import com.qomoi.dto.LoginRequestDTO;
import com.qomoi.dto.RefreshTokenDto;
import com.qomoi.dto.ResetPasswordDto;
import com.qomoi.dto.SignUpRequestDTO;
import com.qomoi.exception.ExistingUserFoundException;
import com.qomoi.exception.MissingFieldException;
import com.qomoi.exception.NotFoundException;
import com.qomoi.jwt.JwtUtils;
import com.qomoi.repository.UserRepository;
import com.qomoi.service.impl.AuthServiceImpl;
import com.qomoi.service.impl.RefreshTokenServiceImpl;
import com.qomoi.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final AuthenticationManager authenticationManager;
    private final AuthServiceImpl authService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AddToCartRepository addToCartRepository;

    @Autowired
    private EncryptDecryptKey encryptDecryptKey;




    public AuthController(UserServiceImpl userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenServiceImpl refreshTokenService,
                          UserRepository userRepository, PasswordEncoder passwordEncoder,AuthServiceImpl authService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> saveUser(@RequestBody SignUpRequestDTO signUpRequestDTO)
            throws MissingFieldException, ExistingUserFoundException {
        return authService.saveUser(signUpRequestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) throws Exception {
        return authService.authenticateUser(loginRequestDTO);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        return authService.logoutUser();
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        return authService.refreshToken(refreshTokenDto);
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleSignup( @RequestBody GoogleSigninRequest googleSigninRequest) throws GeneralSecurityException, IOException, ExistingUserFoundException {
        return authService.googleSignup(googleSigninRequest);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> processForgotPassword(@RequestBody ForgetPasswordDto forgetPasswordDto, Model model) throws MissingFieldException, NotFoundException, JsonProcessingException, NotFoundException {
        return authService.processForgotPassword(forgetPasswordDto, model);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> processResetPassword( @RequestBody ResetPasswordDto resetPasswordDto , Model model) throws MissingFieldException, JsonProcessingException {
        return authService.processResetPassword(resetPasswordDto, model);
    }

    @GetMapping("/get_key")
    public String getSecretKey() throws Exception {
        Long seqId = addToCartRepository.findSequence();
        KeyList keyList = new KeyList();
        keyList.setTokenKey("key"+seqId);
        keyList.setTempName("TEMP"+seqId);
        return encryptDecryptKey.encryptKey(keyList);
    }

    @PostMapping("/store_data")
    public AddToCart storeData(@RequestBody AddToCart addToCart){
        System.out.println(addToCart);
        return addToCartRepository.save(addToCart);
    }

    @GetMapping("/get_stored_data/{key}")
    public AddToCart getStoredData(@PathVariable String key) {
        return addToCartRepository.findBySecretKey(key);
    }

}
