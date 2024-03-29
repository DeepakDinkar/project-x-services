package com.qomoi.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.qomoi.dto.*;
import com.qomoi.entity.AddToCart;
import com.qomoi.entity.PurchaseEntity;
import com.qomoi.entity.StripeSeesion;
import com.qomoi.exception.ExistingUserFoundException;
import com.qomoi.exception.MissingFieldException;
import com.qomoi.exception.NotFoundException;
import com.qomoi.jwt.JwtUtils;
import com.qomoi.modal.KeyList;
import com.qomoi.repository.AddToCartRepository;
import com.qomoi.repository.StripeSeesionRepository;
import com.qomoi.repository.UserRepository;
import com.qomoi.service.EncryptDecryptKey;
import com.qomoi.service.impl.AuthServiceImpl;
import com.qomoi.service.impl.RefreshTokenServiceImpl;
import com.qomoi.service.impl.UserServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import org.springframework.web.servlet.ModelAndView;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.google.gson.JsonSyntaxException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONObject;
import com.stripe.model.*;
import com.stripe.net.Webhook;

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

    @Autowired
    private StripeSeesionRepository stripeSeesionRepository;

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

    @PostMapping("/create-checkout-session")
    public String createCheckoutSession(@RequestBody List<PurchaseEntity> purchaseData) throws StripeException {
        Stripe.apiKey = "sk_test_51Or9WRHIxaQosNkX3sO0uqeuHjxLIP48KdFSimkAmus1lfQNH25UM5i3eSE0DTend1kl037HWymTeEQDqbs4J0ru00B04na9NL";

        String YOUR_DOMAIN = "http://localhost:5173";

        String purchaseDataJson = convertListToJson(purchaseData);

        StripeSeesion seesionData = new StripeSeesion();

        seesionData.setJsonData(purchaseDataJson);

        StripeSeesion response = stripeSeesionRepository.save(seesionData);

        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(YOUR_DOMAIN + "?success=true")
                        .setCancelUrl(YOUR_DOMAIN + "?canceled=true")
                        .setClientReferenceId(String.valueOf(response.getId()))
                        .setAutomaticTax(
                                SessionCreateParams.AutomaticTax.builder()
                                        .setEnabled(true)
                                        .build());

        for (PurchaseEntity lineItem : purchaseData) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("usd")
                                            .setUnitAmount((long) (lineItem.getCourseAmt() * 100L))
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(lineItem.getCourseName())
                                                            .addImage(lineItem.getImageUrl())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build());
        }

        SessionCreateParams params = paramsBuilder.build();
        Session session = Session.create(params);

        response.setPaymentIntent(session.getPaymentIntent());

        stripeSeesionRepository.save(response);

        return session.getUrl();
    }

    @PostMapping("/stripe-webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        String webhookSecret = "whsec_a8b753d721004e52e031c08b8f03135e40aea57f8e1743275fe6312af2e4f6b9";
        System.out.println("Payment listner called: ");
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                System.out.println("checkout.session.completed----------------: ");
                System.out.println(payload);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(payload);
                JsonNode dataNode = rootNode.get("data");
                JsonNode objectNode = dataNode.get("object");
                String clientReferenceId = objectNode.get("client_reference_id").asText();
                StripeSeesion successData = stripeSeesionRepository.findById(Long.valueOf(clientReferenceId)).orElse(null);
                List<PurchaseEntity> response = convertJsonToList(successData.getJsonData());
                addPayment(response);
                System.out.println("Payment succeeded: " + event.getId());
            }
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature verification failed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling webhook");
        }
    }

    private static String convertListToJson(List<PurchaseEntity> purchaseData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(purchaseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PurchaseEntity> convertJsonToList(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<PurchaseEntity> purchaseData = mapper.readValue(json, new TypeReference<List<PurchaseEntity>>() {});
            return purchaseData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PurchaseEntity> addPayment(List<PurchaseEntity> purchasData) {
        List<PurchaseEntity> response = new ArrayList<>();
        String email = "";
        String content = "<p>Hello,</p> <p>You have purchased </p>";
        int count = 1;
        for(PurchaseEntity list : purchasData){
            PurchaseEntity purchaseEntity = userService.findDetails(list.getId());
            purchaseEntity.setStatus("S");
            PurchaseEntity savedPurchaseEntity = userService.savePayment(purchaseEntity);
            if(savedPurchaseEntity != null){
                response.add(savedPurchaseEntity);
            }
            email = savedPurchaseEntity.getEmail();
            content += "<p>" + count + ". Course Name :" + savedPurchaseEntity.getCourseName() + "</p>"
                    + "<p> Venue : " + savedPurchaseEntity.getLocation()
                    + "<p> Date : " + savedPurchaseEntity.getCourseDate()
                    + "<p> Happy learning !!! </p>";
            count++;
        }
        String subject = "Course Purchased";
        try {
            if (StringUtils.hasText(email)) {
                userService.sendEmail(email, subject, content);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }

    @PostMapping("/mail-data")
    public ResponseEntity<?> mailCheck(@RequestBody List<PurchaseEntity> purchasData) {
        String email = "";
        String content = "<p>Hello,</p> <p>You have purchased </p>";
        int count = 1;
        try {
            for (PurchaseEntity list : purchasData) {
                email = list.getEmail();
                content += "<p>" + count + ". Course Name :" + list.getCourseName() + "</p>"
                        + "<p> Venue : " + list.getLocation()
                        + "<p> Date : " + list.getCourseDate();
                count++;
            }
            content += "<p> Happy learning !!! </p>";
            System.out.println(content);

            String subject = "QOMOI - Course Purchased";

            if (StringUtils.hasText(email)) {
                userService.sendEmail(email, subject, content);
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stripe-event-listner")
    public ResponseEntity<String> stripeEventListner(@RequestBody String payload,
                                                     @RequestHeader("Stripe-Signature") String sigHeader) throws JsonProcessingException {
        String webhookSecret = "whsec_QPWmdpvicQnd0yymjw9gkHhvMJGHfWDZ";

        Event event;
        System.out.println(payload);

        JSONObject jsonObject = new JSONObject(payload);
        JSONObject dataObject = jsonObject.getJSONObject("data").getJSONObject("object");

        String eventType = jsonObject.getString("type");
        String paymentStatus = dataObject.getString("payment_status");

        System.out.println("Event Type: " + eventType);
        System.out.println("Payment Status: " + paymentStatus);

        if ("checkout.session.completed".equals(eventType) && "paid".equals(paymentStatus)) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);
            JsonNode dataNode = rootNode.get("data");
            JsonNode objectNode = dataNode.get("object");
            String clientReferenceId = objectNode.get("client_reference_id").asText();
            StripeSeesion successData = stripeSeesionRepository.findById(Long.valueOf(clientReferenceId)).orElse(null);
            List<PurchaseEntity> response = convertJsonToList(successData.getJsonData());
            addPayment(response);
            System.out.println("Payment succeeded: ");
        }else {
            System.out.println("Payment failed: ");
        }


        return ResponseEntity.ok().build();
    }

    @PostMapping("/stripe-payment-listner")
    public ResponseEntity<String> stripePayment(@RequestBody String payload,
                                                     @RequestHeader("Stripe-Signature") String sigHeader) throws JsonProcessingException {
        String endpointSecret = "whsec_H7Unz1g4nD4UmFPKHwZsdwkOW9Q9IP1W";
//        String endpointSecret = "whsec_a8b753d721004e52e031c08b8f03135e40aea57f8e1743275fe6312af2e4f6b9";


        Event event = null;
        JSONObject jsonObject = new JSONObject(payload);
        JSONObject dataObject = jsonObject.getJSONObject("data").getJSONObject("object");
        String payIntent = dataObject.getString("id");
        StripeSeesion stripeSeesionData = stripeSeesionRepository.findByPaymentIntent(payIntent);

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (JsonSyntaxException e) {
            return ResponseEntity.status(400).build();
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).build();
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }
        // Handle the event
        switch (event.getType()) {
            case "charge.captured": {
                System.out.println("charge.captured");
                break;
            }
            case "charge.expired": {
                System.out.println("charge.expired");
                break;
            }
            case "charge.failed": {
                System.out.println("charge.failed");
                break;
            }
            case "charge.pending": {
                System.out.println("charge.pending");
                break;
            }
            case "charge.refunded": {
                System.out.println("charge.refunded");
                break;
            }
            case "charge.succeeded": {
                System.out.println("charge.succeeded");
                break;
            }
            case "charge.updated": {
                System.out.println("charge.updated");
                break;
            }
            case "charge.dispute.closed": {
                System.out.println("charge.dispute.closed");
                break;
            }
            case "charge.dispute.created": {
                System.out.println("charge.dispute.created");
                break;
            }
            case "charge.dispute.funds_reinstated": {
                System.out.println("charge.dispute.funds_reinstated");
                break;
            }
            case "charge.dispute.funds_withdrawn": {
                System.out.println("charge.dispute.funds_withdrawn");
                break;
            }
            case "charge.dispute.updated": {
                System.out.println("charge.dispute.updated");
                break;
            }
            case "charge.refund.updated": {
                System.out.println("charge.refund.updated");
                break;
            }
            case "checkout.session.async_payment_failed": {
                System.out.println("checkout.session.async_payment_failed");
                break;
            }
            case "checkout.session.async_payment_succeeded": {
                System.out.println("checkout.session.async_payment_succeeded");
                break;
            }
            case "checkout.session.completed": {
                System.out.println("checkout.session.completed");
                break;
            }
            case "checkout.session.expired": {
                System.out.println("checkout.session.expired");
                break;
            }
            case "payment_intent.amount_capturable_updated": {
                System.out.println("payment_intent.amount_capturable_updated");
                break;
            }
            case "payment_intent.canceled": {
                StripeSeesion successData = stripeSeesionRepository.findById(Long.valueOf(stripeSeesionData.getId())).orElse(null);
                List<PurchaseEntity> response = convertJsonToList(successData.getJsonData());
                failedPayment(response);
                System.out.println("payment_intent.canceled");
                break;
            }
            case "payment_intent.created": {
                System.out.println("payment_intent.created");
                break;
            }
            case "payment_intent.partially_funded": {
                System.out.println("payment_intent.partially_funded");
                break;
            }
            case "payment_intent.payment_failed": {
                StripeSeesion successData = stripeSeesionRepository.findById(Long.valueOf(stripeSeesionData.getId())).orElse(null);
                List<PurchaseEntity> response = convertJsonToList(successData.getJsonData());
                failedPayment(response);
                System.out.println("payment_intent.payment_failed");
                break;
            }
            case "payment_intent.processing": {
                System.out.println("payment_intent.processing");
                break;
            }
            case "payment_intent.requires_action": {
                System.out.println("payment_intent.requires_action");
                break;
            }
            case "payment_intent.succeeded": {
                System.out.println("Payment Intent: ");

                StripeSeesion successData = stripeSeesionRepository.findById(stripeSeesionData.getId()).orElse(null);
//                System.out.println(successData);
                List<PurchaseEntity> response = convertJsonToList(successData.getJsonData());
//                System.out.println(response);
                if(!successData.getMailTriggered()){
                    System.out.println("Mail initiated: ");
                    addPayment(response);
                }
                successData.setMailTriggered(true);
                stripeSeesionRepository.save(successData);
                System.out.println("Payment succeeded: ");
                System.out.println("payment_intent.succeeded");
                break;
            }

            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
        return ResponseEntity.ok().build();
    }

    public List<PurchaseEntity> failedPayment(List<PurchaseEntity> purchasData) {
        List<PurchaseEntity> response = new ArrayList<>();
        String email = "";
        String content = "<p>Hello,</p>"
                + "<p>We inform you that your purchase has failed due to unsuccessful payment or cancellation.</p>"
                + "<p>Thank you for your understanding.</p>";
        for(PurchaseEntity list : purchasData){
            PurchaseEntity purchaseEntity = userService.findDetails(list.getId());
            email = purchaseEntity.getEmail();
        }
        String subject = "QOMOI - Course Purchase Failed";
        try {
            if (StringUtils.hasText(email)) {
                userService.sendEmail(email, subject, content);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }

}

