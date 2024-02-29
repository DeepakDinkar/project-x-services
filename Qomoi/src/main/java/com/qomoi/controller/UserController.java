package com.qomoi.controller;


import com.qomoi.entity.PurchaseEntity;
import com.qomoi.repository.UserRepository;
import com.qomoi.service.impl.UserServiceImpl;
import com.qomoi.utility.Constants;
import com.qomoi.dto.*;
import com.qomoi.entity.UserDE;
import com.qomoi.exception.NotFoundException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

@RestController
    @RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    @Value("${front.end}")
    private String frontEndUrl;

    public UserController(UserServiceImpl userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/saveProfile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileDto profileDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            if (StringUtils.hasText(email)) {
                UserDE userDE = userService.updateProfile(profileDto, email);
                profileDto.setFirstName(userDE.getFirstName());
                profileDto.setLastName(userDE.getLastName());
                profileDto.setEmail(userDE.getEmailId());
                profileDto.setImageUrl(userDE.getProfileImage());
                profileDto.setAddress1(userDE.getAddress1());
                profileDto.setAddress2(userDE.getAddress2());
                profileDto.setPhoneNo(userDE.getMobile());
                profileDto.setCity(userDE.getCity());
                profileDto.setCountry(userDE.getCountry());
                profileDto.setZipCode(userDE.getZipcode());
                return ResponseEntity.status(HttpStatus.CREATED).body(profileDto);
            }
            throw new EntityNotFoundException("User with email " + email + " not found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/myProfile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            if (StringUtils.hasText(email)) {
                UserDE userDE = userService.getProfile(email);
                ProfileDto profileDto = new ProfileDto();
                profileDto.setFirstName(userDE.getFirstName());
                profileDto.setLastName(userDE.getLastName());
                profileDto.setEmail(userDE.getEmailId());
                profileDto.setImageUrl(userDE.getProfileImage());
                profileDto.setAddress1(userDE.getAddress1());
                profileDto.setAddress2(userDE.getAddress2());
                profileDto.setPhoneNo(userDE.getMobile());
                profileDto.setCity(userDE.getCity());
                profileDto.setCountry(userDE.getCountry());
                profileDto.setZipCode(userDE.getZipcode());
                return ResponseEntity.status(HttpStatus.OK).body(profileDto);
            }
            throw new EntityNotFoundException("User with email " + email + " not found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/savePurchase")
    public ResponseEntity<?> savePurchase(@RequestBody List<PurchaseDto> purchaseDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            String saveDetails = userService.savePurchase(purchaseDto,email);
            if(StringUtils.hasText(saveDetails) && saveDetails.equals("success")){
                return new ResponseEntity<>(new ResponseDto(201, "Record saved successfully"),HttpStatus.OK);
            }
            return new ResponseEntity<>(new ResponseDto(500, "Record not saved"),HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/myPurchase")
    public ResponseEntity<?> getPurchaseInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailId = authentication.getName();
            List<PurchaseResponse> purchaseResponses = userService.myPurchase(emailId);
            if(Objects.nonNull(purchaseResponses)){
                return new ResponseEntity<>(purchaseResponses,HttpStatus.OK);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/myCourses")
    public ResponseEntity<?> getMyCourses() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailId = authentication.getName();
            List<PurchaseResponse> purchaseResponses = userService.myCourses(emailId);
            if(Objects.nonNull(purchaseResponses)){
                return new ResponseEntity<>(purchaseResponses,HttpStatus.OK);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/sendMail")
    public ResponseEntity<?> sendMail(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            List<PurchaseResponse> recentPurchase = userService.recentPurchase(email);

            for (PurchaseResponse purchase : recentPurchase) {
                if (StringUtils.hasText(email)) {
                    try {
                        String content = "<p>Hello,</p>" + "<p>You have purchased "+ purchase.getCoursesName() + " course.</p>"
                                + "<p> Venue : "+purchase.getLocation()
                                + "<p> Date : "+purchase.getCourseDate()
                                + "<p> Happy learning !!! </p>";
                        String subject = "Course Purchased";
                        userService.sendEmail(email, subject, content);
                        model.addAttribute("message", "We have sent a purchase details to your email. Please check.");
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        model.addAttribute("error", "Error while sending email");
                    }
                }
            }
            return ResponseEntity.ok().body(new ResponseDto(200, Constants.MAIL_SENT_SUCCESSFULLY));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/addPayment/{id}")
    public PurchaseEntity addPayment(@PathVariable Long id){
        PurchaseEntity purchaseEntity = userService.findDetails(id);
        purchaseEntity.setStatus("S");
        return userService.savePayment(purchaseEntity);
    }




}