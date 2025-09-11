package com.rookies4.every_moment.board.service;

import com.rookies4.every_moment.board.dto.CommentItem;
import com.rookies4.every_moment.board.dto.PostDetail;
import com.rookies4.every_moment.board.dto.PostListItem;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.board.entity.PostEntity;
import com.rookies4.every_moment.board.repository.CommentRepository;
import com.rookies4.every_moment.board.repository.PostRepository;
import com.rookies4.every_moment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    // 게시글 작성
    @Transactional
    public PostEntity createPost(PostEntity post, UserEntity author) {
        post.setAuthor(author);
        return postRepository.save(post);
    }

    // 게시글 목록 조회 (카테고리 기준)
    @Transactional(readOnly = true)
    public List<PostListItem> listByCategory(String category) {
        return postRepository.findListByCategory(category); // DTO 프로젝션
    }

    // 단일 게시글 조회
    @Transactional(readOnly = true)
    public PostDetail detail(Long id) {
        var p = postRepository.findDetailWithAuthor(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + id));

        var comments = commentRepository.findByPostIdWithAuthor(id).stream()
                .map(c -> new CommentItem(
                        c.getId(), c.getContent(),
                        c.getAuthor().getUsername(), c.getCreatedAt()
                ))
                .toList();

        return new PostDetail(
                p.getId(), p.getCategory(), p.getTitle(), p.getContent(),
                p.getCreatedAt(), p.getAuthor().getUsername(), comments
        );
    }

    // (내부 전용) 엔티티 로딩
    @Transactional(readOnly = true)
    public PostEntity getPostEntity(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + id));
    }

    // 삭제 (Soft delete)
    @Transactional
    public void deletePost(Long id) {
        var post = getPostEntity(id);
        post.setDeleted(true);
        postRepository.save(post);
    }

    // 수정
    @Transactional
    public PostDetail update(Long id, String title, String content, String category, UserEntity editor) {
        var p = getPostEntity(id);

        if (Boolean.TRUE.equals(p.getDeleted())) {
            throw new IllegalStateException("삭제된 글은 수정할 수 없습니다.");
        }

        boolean owner = p.getAuthor().getId().equals(editor.getId());
        boolean admin = "ROLE_ADMIN".equals(editor.getRole());
//        if (!owner && !admin) throw new AccessDeniedException("수정 권한이 없습니다.");

        if (title != null && !title.isBlank())   p.setTitle(title);
        if (content != null && !content.isBlank()) p.setContent(content);
        if (category != null && !category.isBlank()) {
            var allowed = java.util.Set.of("FREE","NOTICE","MATCH","FIND");
            if (!allowed.contains(category)) {
                throw new IllegalArgumentException("잘못된 카테고리: " + category);
            }
            p.setCategory(category);
        }

        return detail(id);
    }
}