package com.qomoi.repository;


import com.qomoi.entity.RefreshToken;
import com.qomoi.entity.UserDE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(UserDE user);
    @Modifying
    int deleteByUser(UserDE user);
}