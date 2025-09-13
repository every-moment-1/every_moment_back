package com.rookies4.every_moment.match.repository;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.match.entity.Match;
import com.rookies4.every_moment.match.entity.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
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

    // 사용자 1과 사용자 2의 ID로 매칭을 조회하는 메서드
    // 사용자 1과 사용자 2의 ID로 매칭을 조회하는 메서드
    @Query("SELECT m FROM Match m WHERE (m.user1.id = :userId AND m.user2.id = :matchUserId) OR (m.user1.id = :matchUserId AND m.user2.id = :userId)")
    List<Match> findByUser1IdAndUser2Id(@Param("userId") Long userId, @Param("matchUserId") Long matchUserId);



}