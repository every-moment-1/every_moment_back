package com.rookies4.every_moment.entity.matching;

import com.rookies4.every_moment.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "preferences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // 설문을 제출한 사용자

    @Column(nullable = false)
    private Integer sleepTime;  // 수면 시간 선호도

    @Column(nullable = false)
    private Integer cleanliness;  // 청결도 선호도

    @Column(nullable = false)
    private Integer noiseSensitivity; // 소음 민감도

    @Column(nullable = false)
    private Integer height; // 높이 선호도

    @Column(nullable = false)
    private Integer roomTemp; // 방 온도 선호도

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


}
