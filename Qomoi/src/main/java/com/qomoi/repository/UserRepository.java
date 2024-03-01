package com.qomoi.repository;


import com.qomoi.entity.UserDE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDE,Long> {

    @Query(value = "SELECT users from UserDE users where users.emailId =?1 or users.mobile = ?2 ")
    public UserDE findUserByEmailAndPhoneNumber(String emailId, String phoneNumber);

    @Query("SELECT u FROM UserDE u WHERE u.emailId = ?1")
    public UserDE findByEmail(String email);

    public UserDE findByResetPasswordToken(String token);

    public boolean existsByEmailId(String email);

}

