package com.rookies4.every_moment.board.service;

import com.rookies4.every_moment.board.dto.CommentItem;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.board.entity.CommentEntity;
import com.rookies4.every_moment.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService; // getPostEntity 사용

    private boolean isAdmin(UserEntity u) {
        return u != null && "ROLE_ADMIN".equals(u.getRole());
    }
    private boolean isOwner(CommentEntity c, UserEntity u) {
        return c != null && c.getAuthor() != null
                && u != null && c.getAuthor().getId().equals(u.getId());
    }

    // 댓글 작성: 엔티티 저장 + id 반환
    @Transactional
    public Long addComment(Long postId, String content, UserEntity author) {
        var post = postService.getPostEntity(postId);
        var c = CommentEntity.builder()
                .post(post)
                .author(author)
                .content(content)
                .build();
        commentRepository.save(c);
        return c.getId();
    }

    // 특정 게시글의 댓글 목록 (작성자 join fetch → DTO로 반환)
    @Transactional(readOnly = true)
    public List<CommentItem> listByPost(Long postId) {
        return commentRepository.findByPostIdWithAuthor(postId)
                .stream()
                .map(c -> new CommentItem(
                        c.getId(),
                        c.getContent(),
                        c.getAuthor().getId(),
                        c.getAuthor().getUsername(),
                        c.getCreatedAt()
                ))
                .toList();
    }

    // ✅ 댓글 삭제: 작성자 또는 관리자만
    @Transactional
    public void deleteComment(Long id, UserEntity actor) {
        var c = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + id));

        if (!isOwner(c, actor) && !isAdmin(actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 삭제 권한이 없습니다.");
        }

        // 하드 삭제
        commentRepository.delete(c);

        // (소프트 삭제를 원하면)
        // c.setDeleted(true);
        // commentRepository.save(c);
    }

    // ✅ 댓글 수정: 작성자 또는 관리자만
    @Transactional
    public CommentItem updateComment(Long id, String content, UserEntity editor) {
        var c = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + id));

        if (!isOwner(c, editor) && !isAdmin(editor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 수정 권한이 없습니다.");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 비어 있을 수 없습니다.");
        }

        c.setContent(content); // updatedAt 없으면 내용만 교체

        // DTO로 반환 (createdAt만 포함)
        return new CommentItem(
                c.getId(),
                c.getContent(),
                c.getAuthor().getId(),
                c.getAuthor().getUsername(),
                c.getCreatedAt()
        );
    }
}