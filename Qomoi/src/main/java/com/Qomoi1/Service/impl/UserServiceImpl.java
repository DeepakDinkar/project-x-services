package com.Qomoi1.Service.impl;


import com.Qomoi1.Repository.RefreshTokenRepository;
import com.Qomoi1.Repository.UserRepository;
import com.Qomoi1.dto.AddressDto;
import com.Qomoi1.dto.SignUpRequestDTO;
import com.Qomoi1.entity.UserDE;
import com.Qomoi1.exception.NotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

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
    private JavaMailSender mailSender;

//    @Autowired
//    private UserPermissionRepository userPermissionRepository;

//    public boolean isEmailExists(String emailId) {
//        System.out.println("Email check service");
//        return userRepository.existsByEmailId(emailId);
//    }

    public UserDE saveUser(SignUpRequestDTO signUpRequestDTO)  {
        UserDE existingUser = userRepository.findUserByEmailAndPhoneNumber(signUpRequestDTO.getEmailId().trim(),
                signUpRequestDTO.getMobile().trim());
//		UserDE existingUser = userRepository.findByEmail(signUpRequestDTO.getEmailId());
        UserDE userregistered = null;
        UserDE userDE = new UserDE();
        userDE.setLastName(signUpRequestDTO.getLastName());
        userDE.setFirstName(signUpRequestDTO.getFirstName());
        userDE.setMobile(signUpRequestDTO.getMobile());
        userDE.setEmailId(signUpRequestDTO.getEmailId());
//        userDE.setStreet(signUpRequestDTO.getStreet());
//        userDE.setCity(signUpRequestDTO.getCity());
//        userDE.setState(signUpRequestDTO.getState());
//        userDE.setPincode(signUpRequestDTO.getPincode());

        userDE.setUserType(signUpRequestDTO.getUserType());
        userDE.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));


        userregistered = userRepository.save(userDE);

        return userregistered;
    }

    public UserDE getByEmailId(String emailId) {
        return userRepository.findByEmail(emailId);
    }

    public UserDE getByEmailIdAndMobileNumber(String emailId, String mobile) {
        return userRepository.findUserByEmailAndPhoneNumber(emailId, mobile);
    }

    public void updateResetPasswordToken(String token, String email) throws NotFoundException {
        UserDE userDE = userRepository.findByEmail(email);
        if (userDE != null) {
            userDE.setUserId(userDE.getUserId());
            userDE.setResetPasswordToken(token);
            userRepository.save(userDE);
        } else {
            throw new NotFoundException("Email is not registered with PV: " + email);
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

        helper.setFrom("support@qomol.com", "Qomol Support");
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }


}
