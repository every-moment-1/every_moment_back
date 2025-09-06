package com.rookies4.every_moment.service;

import com.rookies4.every_moment.controller.dto.AuthDTO.*;
import com.rookies4.every_moment.entity.TokenEntity;
import com.rookies4.every_moment.repository.RefreshTokenRepository;
import com.rookies4.every_moment.exception.ErrorCode;
import com.rookies4.every_moment.security.jwt.JwtTokenProvider;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;
    private final RefreshTokenRepository refreshRepo;

    public RegisterResponse register(RegisterRequest req) {
        if (users.existsByEmail(req.email())) throw new DataIntegrityViolationException("email duplicate");

        UserEntity u = UserEntity.builder()
                .username(req.username())
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .smoking(Boolean.TRUE.equals(req.smoking()))
                .role("ROLE_USER")
                .active(true)
                .build();
        u = users.save(u);

        return new RegisterResponse(
                u.getId(), u.getUsername(), u.getEmail(), u.getSmoking(), u.getCreatedAt().toString()
        );
    }

    public LoginResponse login(LoginRequest req) {
        UserEntity u = users.findByEmail(req.email()).orElseThrow(() -> new BadCredentialsException(ErrorCode.INVALID_CREDENTIALS.message));
        if (!u.getActive()) throw new BadCredentialsException(ErrorCode.INVALID_CREDENTIALS.message);
        if (!encoder.matches(req.password(), u.getPasswordHash())) throw new BadCredentialsException(ErrorCode.INVALID_CREDENTIALS.message);

        String access = jwt.generateAccessToken(u);
        String refresh = jwt.generateRefreshToken(u);

        // save refresh
        var rt = TokenEntity.builder()
                .user(u)
                .token(refresh)
                .expiry(Instant.now().plusSeconds(1209600))
                .revoked(false)
                .build();
        refreshRepo.save(rt);

        var summary = new LoginResponse.UserSummary(u.getId(), u.getUsername(), u.getEmail(), u.getSmoking(), u.getRole());
        return new LoginResponse(access, refresh, summary);
    }

    public RefreshResponse refresh(RefreshRequest req) {
        var token = refreshRepo.findByToken(req.refreshToken()).orElseThrow(() -> new BadCredentialsException(ErrorCode.TOKEN_INVALID.message));
        if (Boolean.TRUE.equals(token.getRevoked()) || token.getExpiry().isBefore(Instant.now())) {
            throw new BadCredentialsException(ErrorCode.TOKEN_EXPIRED.message);
        }
        var user = token.getUser();
        String newAccess = jwt.generateAccessToken(user);
        String newRefresh = jwt.generateRefreshToken(user);

        // rotate: revoke old, save new
        token.setRevoked(true);
        refreshRepo.save(token);
        refreshRepo.save(TokenEntity.builder()
                .user(user)
                .token(newRefresh)
                .expiry(Instant.now().plusSeconds(1209600))
                .revoked(false)
                .build());

        return new RefreshResponse(newAccess, newRefresh);
    }

    public void logout(String refreshToken) {
        refreshRepo.findByToken(refreshToken).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshRepo.save(rt);
        });
    }
}