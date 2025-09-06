package com.rookies4.every_moment.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="refresh_tokens")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TokenEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity user;

    @Column(nullable=false, unique = true, length = 512)
    private String token;

    @Column(nullable=false)
    private Instant expiry;

    @Column(nullable=false)
    private Boolean revoked;

    @Column(name="created_at", nullable=false)
    private java.sql.Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        if (revoked == null) revoked = false;
        if (createdAt == null) createdAt = new java.sql.Timestamp(System.currentTimeMillis());
    }
}