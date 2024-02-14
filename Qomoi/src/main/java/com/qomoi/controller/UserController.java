package com.qomoi.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.qomoi.repository.UserRepository;
import com.qomoi.service.impl.RefreshTokenServiceImpl;
import com.qomoi.service.impl.UserDetailsImpl;
import com.qomoi.service.impl.UserServiceImpl;
import com.qomoi.utility.Constants;
import com.qomoi.utility.Decrypt;
import com.qomoi.dto.*;
import com.qomoi.entity.RefreshToken;
import com.qomoi.entity.UserDE;
import com.qomoi.exception.ExistingUserFoundException;
import com.qomoi.exception.MissingFieldException;
import com.qomoi.exception.NotFoundException;
import com.qomoi.exception.TokenRefreshException;
import com.qomoi.jwt.JwtUtils;
import com.qomoi.validator.ValidateUserFields;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
    @RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;
    //    private final AuthenticationManager authenticationManager;
//    private final JwtUtils jwtUtils;
//    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    @Value("${front.end}")
    private String frontEndUrl;

    public UserController(UserServiceImpl userService, UserRepository userRepository) {
        this.userService = userService;
//        this.authenticationManager = authenticationManager;
//        this.jwtUtils = jwtUtils;
//        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws NotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/saveProfile/{email}")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileDto profileDto, @PathVariable String email) {
        if (StringUtils.hasText(email)) {
            UserDE userDE = userService.updateProfile(profileDto, email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SavedRecordResponseDto(userDE, new ResponseDto(201, "Record saved successfully")));
        }
        throw new EntityNotFoundException("User with email " + email + " not found");
    }

    @GetMapping("/myProfile/{email}")
    public ResponseEntity<?> getProfile(@PathVariable String email) {
        if (StringUtils.hasText(email)) {
            UserDE userDE = userService.getProfile(email);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new SavedRecordResponseDto(userDE, new ResponseDto(200, "User found")));
        }
        throw new EntityNotFoundException("User with email " + email + " not found");
    }


    @PostMapping("/save-address/{id}")
    public ResponseEntity<String> saveAddress(@RequestBody AddressDto addressDto, @PathVariable Long id) {
        if (id != null && addressDto != null) {
            userService.saveAddress(addressDto, id);
            return ResponseEntity.ok("Address saved Successfully");
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @PostMapping("/myProfile/{email}")
//    public ResponseEntity<UserProfileResponse> getMyProfile(@PathVariable String email){
//        return null;
//    }

    @PostMapping("/savePurchase")
    public ResponseEntity<?> savePurchase(@RequestBody List<PurchaseDto> purchaseDto){
        String saveDetails = userService.savePurchase(purchaseDto);
        if(StringUtils.hasText(saveDetails) && saveDetails.equals("success")){
            return new ResponseEntity<>(new ResponseDto(201, "Record saved successfully"),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseDto(500, "Record not saved"),HttpStatus.OK);
    }

    @GetMapping("/myPurchase")
    public ResponseEntity<?> getPurchaseInfo() {
       List<PurchaseResponse> purchaseResponses = userService.myPurchase();
       if(Objects.nonNull(purchaseResponses)){
           return new ResponseEntity<>(purchaseResponses,HttpStatus.OK);
       }
       else{
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
    }

    @PostMapping("/myCourses/{email}")
    public ResponseEntity<CourseResponse> getMyCourses(@PathVariable String email) {
        return null;
    }


    @PostMapping("/sendMail/{email}")
    public ResponseEntity<?> sendMail(@PathVariable String email, Model model) throws MessagingException, UnsupportedEncodingException {
        if (StringUtils.hasText(email)) {
            try {
                String content = "<p>Hello,</p>" + "<p>You have purchased xxx course.</p>"
                        + "<p> Happy learning </p>";
//                    + "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password "
//                    + "or you have not made the request.</p>";
                String subject = "Course Purchased";
                userService.sendEmail(email, subject, content);
                model.addAttribute("message", "We have sent a purchase details to your email. Please check.");
            } catch (MessagingException | UnsupportedEncodingException e) {
                model.addAttribute("error", "Error while sending email");
            }
        }
        return ResponseEntity.ok().body(new ResponseDto(200, Constants.MAIL_SENT_SUCCESSFULLY));
    }
}