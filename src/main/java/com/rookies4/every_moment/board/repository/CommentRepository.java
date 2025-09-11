package com.rookies4.every_moment.board.repository;

import com.rookies4.every_moment.board.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // postId 기준으로 댓글 목록 조회
    List<CommentEntity> findByPostId(Long postId);

    @Query("""
      select c
      from CommentEntity c
      join fetch c.author a
      where c.post.id = :postId
      order by c.id asc
    """)
    List<CommentEntity> findByPostIdWithAuthor(@Param("postId") Long postId);
}
