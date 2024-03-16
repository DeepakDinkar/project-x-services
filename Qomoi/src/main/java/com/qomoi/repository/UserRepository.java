package com.qomoi.repository;


import com.qomoi.entity.UserDE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDE, Long> {

    @Query(value = "SELECT u from UserDE u where u.emailId = :emailId or u.mobile = :phoneNumber")
    UserDE findUserByEmailAndPhoneNumber(@Param("emailId") String emailId, @Param("phoneNumber") String phoneNumber);

    boolean existsByEmailIdOrMobile(String email, String mobile);

    @Query("SELECT u FROM UserDE u WHERE u.emailId = ?1")
    UserDE findByEmail(String email);

    UserDE findByResetPasswordToken(String token);

    boolean existsByEmailIdAndIsNormal(String email, Boolean normal);

}

