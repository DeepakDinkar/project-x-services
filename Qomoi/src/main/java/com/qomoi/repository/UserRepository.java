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

    @Query(value = "SELECT u from UserDE u where u.emailId = :emailId or u.mobile = :phoneNumber")
    public UserDE findUserByEmailAndPhoneNumber(@Param("emailId") String emailId, @Param("phoneNumber") String phoneNumber);

    boolean existsByEmailIdOrMobile(String email, String mobile);

    @Query("SELECT u FROM UserDE u WHERE u.emailId = ?1")
    public UserDE findByEmail(String email);

    public UserDE findByResetPasswordToken(String token);

    public boolean existsByEmailIdAndIsNormal(String email, Boolean normal);

}

