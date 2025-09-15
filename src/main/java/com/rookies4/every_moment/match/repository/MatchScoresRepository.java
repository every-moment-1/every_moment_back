package com.rookies4.every_moment.match.repository;

import com.rookies4.every_moment.match.entity.MatchScores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchScoresRepository extends JpaRepository<MatchScores, Long> {
    Optional<MatchScores> findByMatchId(Long matchId); // 매칭 ID로 점수 찾기
}