package com.rookies4.every_moment.repository.matching;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.matching.Match;
import com.rookies4.every_moment.entity.matching.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    // 특정 사용자와 매칭된 모든 매칭 조회
    List<Match> findByUser1_Id(Long userId);
    List<Match> findByUser2_Id(Long userId);

    // 매칭 상태로 조회 (예: PENDING, ACCEPTED)
    List<Match> findByStatus(String status);

    // 두 사용자 간의 매칭 조회
    List<Match> findByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);

    // 사용자 1, 사용자 2, 매칭 상태로 매칭을 찾는 메서드
    Optional<Match> findByUser1AndUser2AndStatus(UserEntity user1, UserEntity user2, MatchStatus status);
}