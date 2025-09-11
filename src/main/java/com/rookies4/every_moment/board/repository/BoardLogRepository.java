package com.rookies4.every_moment.board.repository;

import com.rookies4.every_moment.board.entity.BoardLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardLogRepository extends JpaRepository<BoardLogEntity, Long> {
    // 특정 사용자 로그 조회
    List<BoardLogEntity> findByUserId(Long userId);

    // 특정 타입(Post/Comment) 로그 조회
    List<BoardLogEntity> findByTargetType(String targetType);
}
