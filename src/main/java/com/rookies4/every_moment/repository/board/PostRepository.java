package com.rookies4.every_moment.repository.board;

import com.rookies4.every_moment.entity.board.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // ✅ 카테고리별 게시글 조회
    List<PostEntity> findByCategory(String category);

    // ✅ 작성자별 게시글 조회 (userId 기반)
    List<PostEntity> findByAuthorId(Long authorId);

    // ✅ 삭제되지 않은 게시글만 조회
    List<PostEntity> findByDeletedFalse();

    // ✅ 카테고리 + 삭제여부 동시 필터링
    List<PostEntity> findByCategoryAndDeletedFalse(String category);
}