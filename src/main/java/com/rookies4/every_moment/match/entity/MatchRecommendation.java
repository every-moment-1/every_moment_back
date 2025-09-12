package com.rookies4.every_moment.match.entity;

import com.rookies4.every_moment.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "match_results")
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

    @ElementCollection
    @CollectionTable(name = "match_result_reasons", joinColumns = @JoinColumn(name = "match_recommendation_id"))
    @Column(name = "reason")
    private List<String> matchReasons; // 매칭 이유 리스트

    @Column(nullable = false)
    private String roommateName;  // 룸메이트 이름 (익명으로 표시)

    @Column(nullable = false)
    private Double preferenceScore;  // 룸메이트 선호도 (0~100 범위)

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}