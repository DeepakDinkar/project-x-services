package com.qomoi.service.impl;


import com.qomoi.entity.UserDE;
import com.qomoi.repository.RefreshTokenRepository;
import com.qomoi.repository.UserRepository;
import com.qomoi.entity.RefreshToken;
import com.qomoi.exception.TokenRefreshException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl {
    @Value("${pv.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        Optional<UserDE> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            UserDE user = userOptional.get();

            Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

            if (existingToken.isPresent()) {
                RefreshToken refreshToken = existingToken.get();
                refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
                return refreshTokenRepository.save(refreshToken);
            } else {
                // Create a new refresh token
                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setUser(user);
                refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
                refreshToken.setToken(UUID.randomUUID().toString());
                return refreshTokenRepository.save(refreshToken);
            }
        } else {
            throw new UsernameNotFoundException("User not found with id: " + userId);
        }
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
