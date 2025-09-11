package com.rookies4.every_moment.match.entity;

import com.rookies4.every_moment.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

//match결과 저장
@Entity
@Table(name = "match_results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_user_id", nullable = false)
    private UserEntity matchUser;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String roomAssignment;  // 예시로 "A동 304호 (DOUBLE)"

    @Column(nullable = false)
    private String roommateName;

    @ElementCollection
    @CollectionTable(name = "match_result_reasons", joinColumns = @JoinColumn(name = "match_result_id"))
    @Column(name = "reason")
    private List<String> matchReasons;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;  // 상태(ACCEPTED, REJECTED, PENDING 등)

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}