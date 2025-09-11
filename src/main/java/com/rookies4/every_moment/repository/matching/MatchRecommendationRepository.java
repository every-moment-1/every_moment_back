package com.rookies4.every_moment.repository.matching;

import com.rookies4.every_moment.entity.matching.MatchRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRecommendationRepository extends JpaRepository<MatchRecommendation, Long> {

    // 매칭 상태로 추천을 조회
    List<MatchRecommendation> findByStatus(String status);

    // 사용자 ID로 추천 조회
    Optional<MatchRecommendation> findByUserId(Long userId);

    // 특정 사용자와 매칭 상태로 추천을 조회
    List<MatchRecommendation> findByUserIdAndStatus(Long userId, String status);

    // 특정 상태의 추천을 최신 순으로 조회
    List<MatchRecommendation> findByStatusOrderByCreatedAtDesc(String status);
}