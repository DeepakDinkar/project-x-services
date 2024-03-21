package com.qomoi.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.qomoi.dto.*;
import com.qomoi.entity.AddToCart;
import com.qomoi.entity.PurchaseEntity;
import com.qomoi.exception.ExistingUserFoundException;
import com.qomoi.exception.MissingFieldException;
import com.qomoi.exception.NotFoundException;
import com.qomoi.jwt.JwtUtils;
import com.qomoi.modal.KeyList;
import com.qomoi.repository.AddToCartRepository;
import com.qomoi.repository.UserRepository;
import com.qomoi.service.EncryptDecryptKey;
import com.qomoi.service.impl.AuthServiceImpl;
import com.qomoi.service.impl.RefreshTokenServiceImpl;
import com.qomoi.service.impl.UserServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

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
    private final AddToCartRepository addToCartRepository;
    private final EncryptDecryptKey encryptDecryptKey;

    public AuthController(UserServiceImpl userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenServiceImpl refreshTokenService, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthServiceImpl authService, AddToCartRepository addToCartRepository, EncryptDecryptKey encryptDecryptKey) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.addToCartRepository = addToCartRepository;
        this.encryptDecryptKey = encryptDecryptKey;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> saveUser(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) throws MissingFieldException, ExistingUserFoundException {
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
    public ResponseEntity<?> googleSignup(@Valid @RequestBody GoogleSigninRequest googleSigninRequest) throws GeneralSecurityException, IOException, ExistingUserFoundException {
        return authService.googleSignup(googleSigninRequest);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> processForgotPassword(@RequestBody ForgetPasswordDto forgetPasswordDto, Model model) throws MissingFieldException, JsonProcessingException, NotFoundException {
        return authService.processForgotPassword(forgetPasswordDto, model);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> processResetPassword(@RequestBody ResetPasswordDto resetPasswordDto, Model model) throws MissingFieldException, JsonProcessingException {
        return authService.processResetPassword(resetPasswordDto, model);
    }

    @GetMapping("/get_key")
    public String getSecretKey() throws Exception {
        Long seqId = addToCartRepository.findSequence();
        KeyList keyList = new KeyList();
        keyList.setTokenKey("key" + seqId);
        keyList.setTempName("TEMP" + seqId);
        return encryptDecryptKey.encryptKey(keyList);
    }

    @PostMapping("/store_data")
    public ResponseEntity<?> storeData(@RequestBody AddToCart addToCart) {
        try {
            AddToCart res = addToCartRepository.findBySecretKey(addToCart.getSecretKey());
            if (res != null) {
                addToCartRepository.deleteSecretKey(addToCart.getSecretKey());
            }
            AddToCart response = addToCartRepository.save(addToCart);
            return ResponseEntity.status(HttpStatus.CREATED).body("Stored successfully!");
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong!");
        }
    }

    @GetMapping("/get_stored_data/{key}")
    public AddToCart getStoredData(@PathVariable String key) {
        return addToCartRepository.findBySecretKey(key);
    }

  // Payment gateway code ---------------

//   =======================================================================================================================

//    @PostMapping("/create-checkout-session")
//    public ModelAndView createCheckoutSession() throws StripeException {
//        Stripe.apiKey = "sk_test_51Or9WRHIxaQosNkX3sO0uqeuHjxLIP48KdFSimkAmus1lfQNH25UM5i3eSE0DTend1kl037HWymTeEQDqbs4J0ru00B04na9NL";
//
//        String YOUR_DOMAIN = "http://localhost/stripe-ui"; // Adjust port accordingly
//        SessionCreateParams params =
//                SessionCreateParams.builder()
//                        .setMode(SessionCreateParams.Mode.PAYMENT)
//                        .setSuccessUrl(YOUR_DOMAIN + "/success.html")
//                        .setCancelUrl(YOUR_DOMAIN + "/cancel.html")
//                        .setAutomaticTax(
//                                SessionCreateParams.AutomaticTax.builder()
//                                        .setEnabled(true)
//                                        .build())
//
//                        .addLineItem(
//                                SessionCreateParams.LineItem.builder()
//                                        .setQuantity(1L)
//                                        .setPrice("{{PRICE_ID}}")
//                                        .build())
//                        .build();
//        Session session = Session.create(params);
//
//        return new ModelAndView("redirect:" + session.getUrl());
//    }

    @PostMapping("/create-checkout-session")
    public ModelAndView createCheckoutSession(@RequestBody List<PurchaseEntity> purchasData) throws StripeException {
        Stripe.apiKey = "sk_test_51Or9WRHIxaQosNkX3sO0uqeuHjxLIP48KdFSimkAmus1lfQNH25UM5i3eSE0DTend1kl037HWymTeEQDqbs4J0ru00B04na9NL";

        String YOUR_DOMAIN = "http://localhost/stripe-ui";

        List<SessionCreateParams.LineItem> stripeLineItems = new ArrayList<>();
        for (PurchaseEntity lineItem : purchasData) {
            stripeLineItems.add(
                    SessionCreateParams.LineItem.builder()
//                            .setQuantity(lineItem.getQuantity())
                            .setName(lineItem.getCourseName())
                            .setPrice(String.valueOf(lineItem.getCourseAmt()))
                            .build()
            );
        }

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(YOUR_DOMAIN + "?success=true")
                        .setCancelUrl(YOUR_DOMAIN + "?canceled=true")
                        .setAutomaticTax(
                                SessionCreateParams.AutomaticTax.builder()
                                        .setEnabled(true)
                                        .build())
                        .addAllLineItem(stripeLineItems)
                        .build();

        Session session = Session.create(params);

        return new ModelAndView("redirect:" + session.getUrl());
    }

    public class LineItem {
        private Long quantity;
        private String priceId;

        // Constructor, getters, and setters
    }


    static {
        Stripe.apiKey = "sk_test_51Or9WRHIxaQosNkX3sO0uqeuHjxLIP48KdFSimkAmus1lfQNH25UM5i3eSE0DTend1kl037HWymTeEQDqbs4J0ru00B04na9NL";
    }

    static class CreatePaymentItem {
        private String id;

        public String getId() {
            return id;
        }
    }

    static class CreatePayment {
        private CreatePaymentItem[] items;

        public CreatePaymentItem[] getItems() {
            return items;
        }
    }

    static class CreatePaymentResponse {
        private String clientSecret;

        public CreatePaymentResponse(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }

    static int calculateOrderAmount(CreatePaymentItem[] items) {
        // Replace this constant with a calculation of the order's amount
        // Calculate the order total on the server to prevent
        // people from directly manipulating the amount on the client
        return 1400;
    }

    @PostMapping("/create-payment-intent")
    public CreatePaymentResponse createPaymentIntent(@RequestBody CreatePayment postBody) {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount((long) calculateOrderAmount(postBody.getItems()))
                        .setCurrency("usd")
                        .build();

        // Create a PaymentIntent with the order amount and currency
        PaymentIntent paymentIntent = null;
        try {
            paymentIntent = PaymentIntent.create(params);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle Stripe API exception
        }

        return new CreatePaymentResponse(paymentIntent.getClientSecret());
    }
}

