package com.qomoi.controller;


import com.qomoi.dto.*;
import com.qomoi.entity.PurchaseEntity;
import com.qomoi.entity.UserDE;
import com.qomoi.exception.NotFoundException;
import com.qomoi.repository.UserRepository;
import com.qomoi.service.impl.UserServiceImpl;
import com.qomoi.utility.Constants;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileDto profileDto) {
        try {
            if (profileDto == null) {
                return ResponseEntity.badRequest().build();
            }
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
    public ResponseEntity<?> savePurchase(@Valid @RequestBody PurchaseInfo purchaseInfo) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            boolean allDetailsAvailable = true;
            List<PurchaseDto> purchaseDtoList = purchaseInfo.getCourses();
            AddressDto addressInfo = purchaseInfo.getAddress();
            Boolean saveAddress = purchaseInfo.getSaveAddress();

            for (PurchaseDto dto : purchaseDtoList) {
//                if (dto.getCourseAmt() == null || dto.getCourseId() == null || dto.getSlug() == null
//                    || dto.getImageUrl() == null || dto.getCourseDate() == null || dto.getTransactionId() == null
//                    || addressInfo.getAddress1() == null || saveAddress == null || addressInfo.getAddress1() == null || addressInfo.getCountry() == null
//                    || addressInfo.getCity() == null) {
//                    allDetailsAvailable = false;
//                    break;
//                }
            }

            if (allDetailsAvailable) {
                List<PurchaseEntity> saveDetails = userService.savePurchase(purchaseDtoList, addressInfo, saveAddress, email);
                return new ResponseEntity<>(saveDetails, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(400, "Incomplete details in request"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/myPurchase")
    public ResponseEntity<?> getPurchaseInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailId = authentication.getName();
            List<PurchaseResponse> purchaseResponses = userService.myPurchase(emailId);
            if (Objects.nonNull(purchaseResponses)) {
                return new ResponseEntity<>(purchaseResponses, HttpStatus.OK);
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
            if (Objects.nonNull(purchaseResponses)) {
                return new ResponseEntity<>(purchaseResponses, HttpStatus.OK);
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
                        String content = "<p>Hello,</p>" + "<p>You have purchased " + purchase.getCoursesName() + " course.</p>"
                                         + "<p> Venue : " + purchase.getLocation()
                                         + "<p> Date : " + purchase.getCourseDate()
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

    @PostMapping("/addPayment")
    public List<PurchaseEntity> addPayment(@RequestBody List<PurchaseEntity> purchasData, Model model) {

        List<PurchaseEntity> response = new ArrayList<>();

        for(PurchaseEntity list : purchasData){
            PurchaseEntity purchaseEntity = userService.findDetails(list.getId());
            purchaseEntity.setStatus("S");
            PurchaseEntity savedPurchaseEntity = userService.savePayment(purchaseEntity);
            if(savedPurchaseEntity != null){
                response.add(savedPurchaseEntity);
            }
        }



        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            List<PurchaseResponse> recentPurchase = userService.recentPurchase(email);

            for (PurchaseResponse purchase : recentPurchase) {
                if (StringUtils.hasText(email)) {
                    String content = "<p>Hello,</p>" + "<p>You have purchased " + purchase.getCoursesName() + " course.</p>"
                                     + "<p> Venue : " + purchase.getLocation()
                                     + "<p> Date : " + purchase.getCourseDate()
                                     + "<p> Happy learning !!! </p>";
                    String subject = "Course Purchased";

                    // Send email
                    userService.sendEmail(email, subject, content);

                    model.addAttribute("message", "We have sent a purchase details to your email. Please check.");
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error while sending email: " + e.getMessage());
        }

        return response;
    }

}