package com.rookies4.every_moment.match.repository;

import com.rookies4.every_moment.match.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    // 상태별 매칭 결과 조회 (예: PENDING 상태)
    List<MatchResult> findByStatus(String status);

    // 매칭 결과를 점수 기준으로 조회
    List<MatchResult> findByUserIdOrderByScoreDesc(Long userId);

    // 사용자와 관련된 모든 매칭 결과
    List<MatchResult> findByUserId(Long userId);

    // 사용자와 상대방 매칭 결과 (여러 결과 반환)
    @Query("SELECT m FROM MatchResult m WHERE m.user.id = :userId AND m.matchUser.id = :matchUserId")
    List<MatchResult> findByUserIdAndMatchUserId(@Param("userId") Long userId, @Param("matchUserId") Long matchUserId);

    // ==============================
    // ✅ 관리자 전용: 페어(순서 무시) + match_id 기준으로 "최신 1건"만 전체 조회
    // ==============================
    @Query(value = """
        SELECT mr.*
        FROM match_results mr
        JOIN (
            SELECT MAX(id) AS id
            FROM match_results
            GROUP BY
                LEAST(user_id, match_user_id),
                GREATEST(user_id, match_user_id),
                match_id
        ) latest ON latest.id = mr.id
        ORDER BY mr.created_at DESC
        """, nativeQuery = true)
    List<MatchResult> findAllCurrent();
}
