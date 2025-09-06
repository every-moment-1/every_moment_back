package com.rookies4.every_moment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name="uk_users_email", columnNames = "email")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserEntity {

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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

//    //DB default에 의존함
//    @PrePersist
//    public void prePersist() {
//        var now = new java.sql.Timestamp(System.currentTimeMillis());
//        if (active == null) active = true;
//        if (role == null) role = "ROLE_USER";
//        if (smoking == null) smoking = false;
//    }
}