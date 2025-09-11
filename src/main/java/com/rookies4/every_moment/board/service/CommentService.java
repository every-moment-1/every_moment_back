package com.rookies4.every_moment.board.service;

import com.rookies4.every_moment.board.dto.CommentItem;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.board.entity.CommentEntity;
import com.rookies4.every_moment.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService; // getPostEntity 사용

    // 댓글 작성: 엔티티 저장 + id 반환
    @Transactional
    public Long addComment(Long postId, String content, UserEntity author) {
        var post = postService.getPostEntity(postId); // 🔁 여기!
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
        return commentRepository.findByPostIdWithAuthor(postId) // 🔁 여기!
                .stream()
                .map(c -> new CommentItem(
                        c.getId(),
                        c.getContent(),
                        c.getAuthor().getUsername(),
                        c.getCreatedAt()
                ))
                .toList();
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long id) {
        var c = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + id));
        commentRepository.delete(c);
    }

    @Transactional
    public CommentItem updateComment(Long id, String content, UserEntity editor) {
        var c = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + id));

//        boolean owner = c.getAuthor().getId().equals(editor.getId());
//        boolean admin = "ROLE_ADMIN".equals(editor.getRole());
//        if (!owner && !admin) throw new AccessDeniedException("수정 권한이 없습니다.");

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 비어 있을 수 없습니다.");
        }

        c.setContent(content); // updatedAt 없으니 그냥 내용만 교체

        // DTO로 반환 (createdAt만 포함)
        return new CommentItem(
                c.getId(),
                c.getContent(),
                c.getAuthor().getUsername(),
                c.getCreatedAt()
        );
    }
}
