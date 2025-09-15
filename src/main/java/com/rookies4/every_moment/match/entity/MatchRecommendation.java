package com.rookies4.every_moment.match.entity;

import com.rookies4.every_moment.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_recs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;  // 추천된 사용자 (userId)

    @Column(nullable = false)
    private String username;  // 추천된 사용자 이름 (익명으로 표시)

    @Column(nullable = false)
    private Integer score;  // 매칭 점수 (적합도)

    @Column(nullable = false)
    private String status;  // 매칭 상태 (PENDING, ACCEPTED, REJECTED, SWAP_REQUESTED)

    @Column(nullable = false)
    private String roommateName;  // 룸메이트 이름 (익명으로 표시)

    @Column(nullable = false)
    private Double preferenceScore;  // 룸메이트 선호도 (0~100 범위)

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false)
    private LocalDateTime updatedAt;
}