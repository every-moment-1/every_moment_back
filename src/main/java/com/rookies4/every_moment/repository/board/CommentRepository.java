package com.rookies4.every_moment.repository.board;

import com.rookies4.every_moment.entity.board.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // postId 기준으로 댓글 목록 조회
    List<CommentEntity> findByPostId(Long postId);
}
