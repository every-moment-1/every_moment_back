package com.rookies4.every_moment.repository;

import com.rookies4.every_moment.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByToken(String token);
    void deleteByUser_Id(Long userId);
}