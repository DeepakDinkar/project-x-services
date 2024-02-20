package com.qomoi.service.impl;


import com.qomoi.dto.*;
import com.qomoi.entity.MyCoursesEntity;
import com.qomoi.entity.PurchaseEntity;
import com.qomoi.jwt.JwtUtils;
import com.qomoi.repository.MyCourseRepository;
import com.qomoi.repository.PurchaseRepository;
import com.qomoi.repository.RefreshTokenRepository;
import com.qomoi.repository.UserRepository;
import com.qomoi.entity.UserDE;
import com.qomoi.exception.NotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    private MyCourseRepository myCourseRepository;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public UserDE saveUser(SignUpRequestDTO signUpRequestDTO) throws Exception {
        UserDE existingUser = userRepository.findUserByEmailAndPhoneNumber(signUpRequestDTO.getEmail().trim(),
                signUpRequestDTO.getMobile().trim());
        UserDE userregistered = null;
        UserDE userDE = new UserDE();
        userDE.setLastName(signUpRequestDTO.getLastName());
        userDE.setFirstName(signUpRequestDTO.getFirstName());
        userDE.setMobile(signUpRequestDTO.getMobile());
        userDE.setEmailId(signUpRequestDTO.getEmail());
        userDE.setUserType(signUpRequestDTO.getUserType());
        userDE.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        userDE.setIsNormal(true);
        userregistered = userRepository.save(userDE);

        return userregistered;
    }

    public UserDE getByEmailId(String emailId) {
        return userRepository.findByEmail(emailId);
    }

    public UserDE getByEmailIdAndMobileNumber(String emailId, String mobile) {
        return userRepository.findUserByEmailAndPhoneNumber(emailId, mobile);
    }

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


    public void deleteUser(Long id) throws NotFoundException {
        Optional<UserDE> userDE = userRepository.findById(id);

        if (userDE.isPresent()) {
            refreshTokenRepository.deleteByUser(userDE.get());
            userRepository.delete(userDE.get());
        } else {
            throw new NotFoundException("User not found with id: " + id);
        }
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
           user.setProfileImage(profileDto.getImageUrl());
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
            userDE.setResetPasswordToken(token);
            userRepository.save(userDE);
        } else {
            throw new NotFoundException("Email is not registered with Qomoi: " + email);
        }
    }

    public String savePurchase(List<PurchaseDto> purchaseDto, String email) {


        if (StringUtils.hasText(email)) {
            for (PurchaseDto purchase : purchaseDto) {

                String sql = "SELECT c.campaign_template_course_name " +
                        "FROM courses c " +
                        "WHERE c.id = :courseId";

                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("courseId", purchase.getCourseId());

                String courseName = (String) query.getSingleResult();

                PurchaseEntity purchaseEntity = new PurchaseEntity();
                purchaseEntity.setCourseId(purchase.getCourseId());
                purchaseEntity.setCourseName(courseName);
                purchaseEntity.setCourseDate(purchase.getCourseDate());
                purchaseEntity.setTransactionId(purchase.getTransactionId());
                purchaseEntity.setEmail(email);
                purchaseEntity.setLocation(purchase.getLocation());
                purchaseEntity.setCourseAmt(purchase.getCourseAmt());
                purchaseEntity.setPurchaseDate(new Date());
                purchaseRepository.save(purchaseEntity);

                boolean emailExists = myCourseRepository.existsByEmail(email);
                if (!emailExists) {
                    MyCoursesEntity myCourses = new MyCoursesEntity();
                    myCourses.setEmail(email);
                    List<String> courseNames = new LinkedList<>();
                    courseNames.add(courseName);
                    myCourses.setAllCourses(courseNames);
                    myCourseRepository.save(myCourses);
                } else {
                    MyCoursesEntity myCourses = myCourseRepository.findByEmail(email);
                    List<String> courseNames = myCourses.getAllCourses();
                        courseNames.add(courseName);
                        myCourses.setAllCourses(courseNames);
                        myCourseRepository.save(myCourses);

                }
            }
            return "success";
        }
        return "fail";
    }



    public List<PurchaseResponse> myPurchase (String email) {

        StringBuilder sql = new StringBuilder("SELECT p.course_name, p.location, p.course_date, p.course_amt, p.transaction_id, p.purchase_date");
        sql.append(" FROM purchase p ");
        sql.append(" WHERE email = ? ");

        List<PurchaseResponse> list = this.jdbcTemplate.query(sql.toString(), new Object[]{email},
                new RowMapper<PurchaseResponse>() {
                    @Override
                    public PurchaseResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PurchaseResponse purchaseResponse = new PurchaseResponse();
                        purchaseResponse.setCoursesName(rs.getString("course_name"));
                        purchaseResponse.setCourseAmt(rs.getString("course_amt"));
                        String location = rs.getString("location");
                        if(location.isEmpty()){
                            purchaseResponse.setLocation(null);
                        }else{
                            purchaseResponse.setLocation(location);
                        }
                        purchaseResponse.setCourseDate(rs.getDate("course_date"));
                        purchaseResponse.setTransactionId(rs.getString("transaction_id"));
                        purchaseResponse.setPurchasedDate(rs.getDate("purchase_date"));
                        return purchaseResponse;
                    }
                });
        return list;
    }

    public List<PurchaseResponse> myCourses (String email) {

        StringBuilder sql = new StringBuilder("SELECT p.course_name, p.location, p.course_date, p.course_amt, p.transaction_id, p.purchase_date");
        sql.append(" FROM purchase p ");
        sql.append(" WHERE email = ?  and course_date < current_date ");
        sql.append(" ORDER BY course_date DESC ");

        List<PurchaseResponse> list = this.jdbcTemplate.query(sql.toString(), new Object[]{email},
                new RowMapper<PurchaseResponse>() {
                    @Override
                    public PurchaseResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PurchaseResponse purchaseResponse = new PurchaseResponse();
                        purchaseResponse.setCoursesName(rs.getString("course_name"));
                        purchaseResponse.setCourseAmt(rs.getString("course_amt"));
                        String location = rs.getString("location");
                        if(location.isEmpty()){
                            purchaseResponse.setLocation(null);
                        }else{
                            purchaseResponse.setLocation(location);
                        }
                        purchaseResponse.setCourseDate(rs.getDate("course_date"));
                        purchaseResponse.setTransactionId(rs.getString("transaction_id"));
                        purchaseResponse.setPurchasedDate(rs.getDate("purchase_date"));
                        return purchaseResponse;
                    }
                });
        return list;
    }


    public List<PurchaseResponse> recentPurchase(String emailId){

        StringBuilder sql = new StringBuilder("SELECT c.campaign_template_course_name, p.location, p.course_date, p.course_amt, p.transaction_id, p.purchase_date");
        sql.append(" FROM purchase p ");
        sql.append(" JOIN courses c ON c.id = p.course_id ");
        sql.append(" WHERE email = ?  AND DATE(p.purchase_date) = CURRENT_DATE ");

        List<PurchaseResponse> list = this.jdbcTemplate.query(sql.toString(), new Object[]{emailId},
                new RowMapper<PurchaseResponse>() {
                    @Override
                    public PurchaseResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PurchaseResponse purchaseResponse = new PurchaseResponse();
                        purchaseResponse.setCoursesName(rs.getString("campaign_template_course_name"));
                        purchaseResponse.setCourseAmt(rs.getString("course_amt"));
                        purchaseResponse.setLocation(rs.getString("location"));
                        purchaseResponse.setCourseDate(rs.getDate("course_date"));
                        purchaseResponse.setTransactionId(rs.getString("transaction_id"));
                        purchaseResponse.setPurchasedDate(rs.getDate("purchase_date"));
                        return purchaseResponse;
                    }
                });
        return list;

    }

    public List<PurchaseResponse> getReminder(){

        StringBuilder sql = new StringBuilder(" SELECT c.campaign_template_course_name, p.email, p.course_date, p.location ");
        sql.append(" FROM purchase p ");
        sql.append(" JOIN courses c ON c.id = p.course_id ");
        sql.append(" WHERE DATE(course_date) = CURRENT_DATE + INTERVAL '5 days' ");

        List<PurchaseResponse> list = this.jdbcTemplate.query(sql.toString(), new Object[]{},
                new RowMapper<PurchaseResponse>() {
                    @Override
                    public PurchaseResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PurchaseResponse purchaseResponse = new PurchaseResponse();
                        purchaseResponse.setCoursesName(rs.getString("campaign_template_course_name"));
                        purchaseResponse.setLocation(rs.getString("location"));
                        purchaseResponse.setCourseDate(rs.getDate("course_date"));
                        return purchaseResponse;
                    }
                });
        return list;

    }



}
