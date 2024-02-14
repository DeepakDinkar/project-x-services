package com.qomoi.service.impl;


import com.qomoi.dto.*;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.PurchaseEntity;
import com.qomoi.jwt.JwtUtils;
import com.qomoi.repository.PurchaseRepository;
import com.qomoi.repository.RefreshTokenRepository;
import com.qomoi.repository.UserRepository;
import com.qomoi.utility.Constants;
import com.qomoi.utility.Decrypt;
import com.qomoi.entity.UserDE;
import com.qomoi.exception.NotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class UserServiceImpl {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String CHARACTERS = "OIUMHUpokjh1645GTT55868RCRCIqwsdcfvTVYTITceztz183rfghjo";
    private static final Random RANDOM = new SecureRandom();

    public UserDE saveUser(SignUpRequestDTO signUpRequestDTO) throws Exception {
        Decrypt cryptPass = new Decrypt();
        UserDE existingUser = userRepository.findUserByEmailAndPhoneNumber(signUpRequestDTO.getEmailId().trim(),
                signUpRequestDTO.getMobile().trim());
        UserDE userregistered = null;
        UserDE userDE = new UserDE();
        userDE.setLastName(signUpRequestDTO.getLastName());
        userDE.setFirstName(signUpRequestDTO.getFirstName());
        userDE.setMobile(signUpRequestDTO.getMobile());
        userDE.setEmailId(signUpRequestDTO.getEmailId());
        userDE.setUserType(signUpRequestDTO.getUserType());
        String salt = getNextSalt(Constants.CHARACTER_LENGTH);
        userDE.setSalt(salt);
        String rawPass = cryptPass.decrypt(signUpRequestDTO.getPassword());
        userDE.setPassword(passwordEncoder.encode(salt+rawPass));
        userDE.setIsNormal(true);
        userregistered = userRepository.save(userDE);

        return userregistered;
    }

    private static String getNextSalt(int length) {
        StringBuilder salt = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            salt.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return salt.toString();
    }
    public UserDE getByEmailId(String emailId) {
        return userRepository.findByEmail(emailId);
    }

    public UserDE getByEmailIdAndMobileNumber(String emailId, String mobile) {
        return userRepository.findUserByEmailAndPhoneNumber(emailId, mobile);
    }

//    public void updateResetPasswordToken(String token, String email) throws NotFoundException {
//        UserDE userDE = userRepository.findByEmail(email);
//        if (userDE != null) {
//            userDE.setUserId(userDE.getUserId());
//            userDE.setResetPasswordToken(token);
//            userRepository.save(userDE);
//        } else {
//            throw new NotFoundException("Email is not registered with PV: " + email);
//        }
//    }


    public String saveGoogleLogin(GoogleTokenResponse googleTokenResponse){
      if(userRepository.findByEmail(googleTokenResponse.getEmail()) == null) {
          UserDE userDE = new UserDE();
          userDE.setLastName(googleTokenResponse.getFamily_name());
          userDE.setFirstName(googleTokenResponse.getGiven_name());
          userDE.setEmailId(googleTokenResponse.getEmail());
          userDE.setIsGoogle(true);
          userRepository.save(userDE);
          return "saved successfully ";
      }
      else {
          return "user already exists ";
      }
    }

    public UserDE getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(UserDE userDE, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);

        userDE.setPassword(encodedPassword);
        userDE.setResetPasswordToken(null);

        userRepository.save(userDE);
    }
    public Optional<UserDE> getEmailId(String emailId) {
        return userRepository.findByEmailIds(emailId); }

    public List<UserDE> getAllDetails() {
        return userRepository.findAllDetails();
    }
    public List<UserDE> getAllDetail() {
        List<UserDE>  userDE = userRepository.findAllAdmin();
        return userDE;
    }

    public UserDE getUserId(Long id) throws NotFoundException {
        Optional<UserDE> userId = userRepository.findById(id);
        if (userId.isPresent()) {
            UserDE userDE = userId.get();
            return userDE;
        } else {
            throw new NotFoundException("User not found with id: " + id);
        }
    }
    public void deleteUser(Long id) throws NotFoundException {
        Optional<UserDE> userDE = userRepository.findById(id);

        if (userDE.isPresent()) {
            refreshTokenRepository.deleteByUser(userDE.get());
            userRepository.delete(userDE.get());
        } else {
            throw new NotFoundException("User not found with id: " + id);
        }
    }
    public boolean isEmailIdExists(String emailId) {
        return userRepository.existsByEmailId(emailId);
    }
    public boolean isMobileExists(String mobile) {
        return userRepository.existsByMobile(mobile);
    }

    public UserDE updateProfile(ProfileDto profileDto, String email){

       UserDE user = userRepository.findByEmail(email);

       if(Objects.nonNull(user)){
           user.setFirstName(profileDto.getFirstName());
           user.setLastName(profileDto.getLastName());
           user.setAddress1(profileDto.getAddress1());
           user.setAddress2(profileDto.getAddress2());
           user.setCountry(profileDto.getCountry());
           user.setCity(profileDto.getCity());
           user.setZipcode(profileDto.getZipCode());
           return userRepository.save(user);
       }
        throw new EntityNotFoundException("User with email " + email + " not found");
    }

    public UserDE getProfile(String email){
        UserDE user = userRepository.findByEmail(email);
            if(Objects.nonNull(user)){
                return user;
            }
        throw new UsernameNotFoundException("User with email " + email + " not found");
    }

    public String saveAddress(AddressDto addressDto, Long id) {

        Optional<UserDE> userVal = userRepository.findById(id);
        if(userVal.isPresent()){
            userVal.get().setAddress1(addressDto.getAddress1());
            userVal.get().setAddress2(addressDto.getAddress2());
            userVal.get().setCity(addressDto.getCity());
            userVal.get().setState(addressDto.getState());
            userVal.get().setZipcode(addressDto.getZipcode());
            userRepository.save(userVal.get());
            return "Address saved successfully";
        }
        else{
            return "User not found";
        }
    }

    public void sendEmail(String recipientEmail, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("support@rangachari.com", "Qomoi Support");
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public void updateResetPasswordToken(String token, String email) throws NotFoundException {
        UserDE userDE = userRepository.findByEmail(email);
        if (userDE != null) {
//            userDE.setUserId(userDE.getUserId());
            userDE.setResetPasswordToken(token);
            userRepository.save(userDE);
        } else {
            throw new NotFoundException("Email is not registered with PV: " + email);
        }
    }

    public String savePurchase(List<PurchaseDto> purchaseDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailId = authentication.getName();

        for ( PurchaseDto purchase : purchaseDto) {

            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCourseId(purchase.getCourseId());
            purchaseEntity.setCourseDate(purchase.getCourseDate());
            purchaseEntity.setTransactionId(purchase.getTransactionId());
            purchaseEntity.setEmail(emailId);
            purchaseEntity.setLocation(purchase.getLocation());
            purchaseEntity.setCourseAmt(purchase.getCourseAmt());
            purchaseRepository.save(purchaseEntity);
        }
        return "success";
    }

    public List<PurchaseResponse> myPurchase () {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailId = authentication.getName();

        StringBuilder sql = new StringBuilder("SELECT c.campaign_template_course_name, p.location, p.course_date, p.course_amt ");
        sql.append("FROM purchase p ");
        sql.append("JOIN courses c ON c.id = p.course_id ");
        sql.append("WHERE email = ?");

        List<PurchaseResponse> list = this.jdbcTemplate.query(sql.toString(), new Object[]{emailId},
                new RowMapper<PurchaseResponse>() {
                    @Override
                    public PurchaseResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PurchaseResponse purchaseResponse = new PurchaseResponse();
                        purchaseResponse.setCoursesName(rs.getString("campaign_template_course_name"));
                        purchaseResponse.setCourseAmt(rs.getString("course_amt"));
                        purchaseResponse.setLocation(rs.getString("location"));
                        purchaseResponse.setCourseDate(rs.getDate("course_date"));
                        return purchaseResponse;
                    }
                });
        return list;
    }
}
