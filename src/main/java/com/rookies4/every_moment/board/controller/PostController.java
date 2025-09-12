package com.rookies4.every_moment.board.controller;

import com.rookies4.every_moment.board.dto.PostListItem;
import com.rookies4.every_moment.board.dto.PostDetail;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.board.entity.PostEntity;
import com.rookies4.every_moment.service.UserService;
import com.rookies4.every_moment.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    // 글 작성: 요청 DTO → 엔티티 변환 후 저장, 응답은 id만 반환 (엔티티 직렬화 방지)
    @PostMapping
    public ResponseEntity<CreateRes> createPost(@RequestBody CreateReq req, Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);

        PostEntity toSave = PostEntity.builder()
                .category(req.category())
                .title(req.title())
                .content(req.content())
                .build();

        PostEntity saved = postService.createPost(toSave, user);
        URI location = URI.create("/api/posts/" + saved.getId());
        return ResponseEntity.created(location).body(new CreateRes(saved.getId()));
    }

    // 목록: DTO로 반환 (직렬화 안전)
    @GetMapping
    public List<PostListItem> getPosts(@RequestParam(defaultValue = "FREE") String category) {
        return postService.listByCategory(category);
    }

    // 상세: DTO로 반환 (직렬화 안전)
    @GetMapping("/{id}")
    public PostDetail getPost(@PathVariable Long id) {
        return postService.detail(id);
    }

    // 삭제: 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication auth) {
        UserEntity me = userService.getCurrentUser(auth);
        postService.deletePost(id, me);   // ✅ actor 전달
        return ResponseEntity.noContent().build();
    }

    // ✅ 글 수정 (부분 수정: 제목/내용/카테고리 중 보내준 것만 반영)
    @PatchMapping("/{id}")
    public PostDetail updatePost(
            @PathVariable Long id,
            @RequestBody UpdateReq req,
            Authentication auth
    ) {
        UserEntity user = userService.getCurrentUser(auth);
        return postService.update(id, req.title(), req.content(), req.category(), user);
    }

    // ===== 요청/응답 DTO =====
    public record CreateReq(String category, String title, String content) {}
    public record CreateRes(Long id) {}
    // ✅ 수정 바디용 DTO (보내준 필드만 수정)
    public record UpdateReq(String title, String content, String category) {}
}