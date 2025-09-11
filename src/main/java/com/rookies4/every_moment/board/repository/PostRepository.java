package com.rookies4.every_moment.board.repository;

import com.rookies4.every_moment.board.dto.PostListItem;
import com.rookies4.every_moment.board.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // ✅ 카테고리별 게시글 조회
    List<PostEntity> findByCategory(String category);

    // ✅ 작성자별 게시글 조회 (userId 기반)
    List<PostEntity> findByAuthorId(Long authorId);

    // ✅ 삭제되지 않은 게시글만 조회
    List<PostEntity> findByDeletedFalse();

    // ✅ 목록: DTO 프로젝션
    @Query("""
      select new com.rookies4.every_moment.board.dto.PostListItem(
        p.id, p.category, p.title, p.createdAt, a.username
      )
      from PostEntity p
      join p.author a
      where p.deleted = false and p.category = :category
      order by p.id desc
    """)
    List<PostListItem> findListByCategory(@Param("category") String category);

    // ✅ 상세: author까지 한 번에 로딩 (엔티티 반환 → 서비스에서 DTO로 변환)
    @Query("""
      select p
      from PostEntity p
      join fetch p.author a
      where p.id = :id and p.deleted = false
    """)
    Optional<PostEntity> findDetailWithAuthor(@Param("id") Long id);
}