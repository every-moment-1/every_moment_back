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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final Set<String> ALLOWED = Set.of("FREE", "NOTICE", "MATCH", "FIND");

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    private boolean isAdmin(UserEntity u) {
        return u != null && "ROLE_ADMIN".equals(u.getRole());
    }

    private boolean isOwner(PostEntity p, UserEntity u) {
        return p != null && u != null && p.getAuthor() != null
                && p.getAuthor().getId().equals(u.getId());
    }

    private boolean isNotice(String category) {
        return "NOTICE".equalsIgnoreCase(String.valueOf(category));
    }

    // 게시글 작성 (공지 NOTICE는 관리자만)
    @Transactional
    public PostEntity createPost(PostEntity post, UserEntity author) {
        if (post.getCategory() == null || !ALLOWED.contains(post.getCategory())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 카테고리");
        }
        if (isNotice(post.getCategory()) && !isAdmin(author)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공지 작성 권한이 없습니다.");
        }
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
                        c.getAuthor().getId(), c.getAuthor().getUsername(), c.getCreatedAt()
                ))
                .toList();

        return new PostDetail(
                p.getId(), p.getCategory(), p.getTitle(), p.getContent(),
                p.getCreatedAt(), p.getUpdatedAt(),p.getAuthor().getId(), p.getAuthor().getUsername(), comments
        );
    }

    // (내부 전용) 엔티티 로딩 – 댓글 등에서 필요할 때만 사용
    @Transactional(readOnly = true)
    public PostEntity getPostEntity(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + id));
    }

    // 삭제(Soft delete): 작성자 또는 관리자만
    @Transactional
    public void deletePost(Long id, UserEntity actor) {
        var post = getPostEntity(id);
        boolean owner = isOwner(post, actor);
        boolean admin = isAdmin(actor);
        if (!owner && !admin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }
        post.setDeleted(true);
        postRepository.save(post);
    }

    // 수정: 작성자 또는 관리자만 (+ NOTICE로 변경은 관리자만)
    @Transactional
    public PostDetail update(Long id, String title, String content, String category, UserEntity editor) {
        var p = getPostEntity(id);

        if (Boolean.TRUE.equals(p.getDeleted())) {
            throw new IllegalStateException("삭제된 글은 수정할 수 없습니다.");
        }

        boolean owner = isOwner(p, editor);
        boolean admin = isAdmin(editor);
        if (!owner && !admin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        if (title != null && !title.isBlank()) {
            p.setTitle(title);
        }
        if (content != null && !content.isBlank()) {
            p.setContent(content);
        }
        if (category != null && !category.isBlank()) {
            if (!ALLOWED.contains(category)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 카테고리: " + category);
            }
            // 공지로 변경은 관리자만 허용
            if (isNotice(category) && !admin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공지로 변경할 권한이 없습니다.");
            }
            p.setCategory(category);
        }

        // @LastModifiedDate 로 updatedAt 자동 갱신
        return detail(id);
    }
}