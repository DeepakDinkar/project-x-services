package com.qomoi.Repository;


import com.qomoi.entity.UserDE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDE,Long> {
    boolean existsByEmailId(String emailId);
    boolean existsByMobile(String mobile);
    @Query(value = "SELECT users from UserDE users where users.emailId =?1 or users.mobile = ?2 ")
    public UserDE findUserByEmailAndPhoneNumber(String emailId, String phoneNumber);

    Optional<UserDE> findByEmailId(String username);
    @Query("SELECT cg FROM UserDE cg WHERE cg.emailId = :emailId")
    Optional<UserDE> findByEmailIds(@Param("emailId") String emailId);

    @Query("SELECT u FROM UserDE u WHERE u.emailId = ?1")
    public UserDE findByEmail(String email);

    public UserDE findByResetPasswordToken(String token);

    @Query(value = "select * from users where user_type != '1' AND user_type != '0' ",nativeQuery = true)
    List<UserDE> findAllDetails();
    @Query(value = "select * from users where user_type != '0' order by user_id desc",nativeQuery = true)
    List<UserDE> findAllAdmin();

}

