package com.rookies4.every_moment.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_username", columnList = "username"),
                @Index(name = "idx_users_email", columnList = "email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name="uk_users_username", columnNames = "username"),
                @UniqueConstraint(name="uk_users_email", columnNames = "email")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=40)
    private String username;

    @Column(nullable=false, length=100)
    private String email;

    @Column(name="password_hash", nullable=false, length=255)
    private String passwordHash;

    @Column(nullable=false)
    private Boolean smoking;

    @Column(nullable=false, length=20)
    private String role;

    @Column(nullable=false)
    private Boolean active;

    @Column(name="created_at", nullable=false)
    private java.sql.Timestamp createdAt;

    @Column(name="updated_at", nullable=false)
    private java.sql.Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        var now = new java.sql.Timestamp(System.currentTimeMillis());
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (active == null) active = true;
        if (role == null) role = "ROLE_USER";
        if (smoking == null) smoking = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new java.sql.Timestamp(System.currentTimeMillis());
    }
}