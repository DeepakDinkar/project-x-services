package com.qomoi.service.impl;


import com.qomoi.entity.RefreshToken;
import com.qomoi.entity.UserDE;
import com.qomoi.repository.RefreshTokenRepository;
import com.qomoi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${pv.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

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

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
