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

    // 글 작성
    @PostMapping
    public ResponseEntity<CreateRes> createPost(@RequestBody CreateReq req, Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);

        PostEntity toSave = PostEntity.builder()
                .category(req.category())
                .title(req.title())
                .content(req.content())
                .status(req.status() != null ? req.status() : "NORMAL")
                .build();

        PostEntity saved = postService.createPost(toSave, user);
        URI location = URI.create("/api/posts/" + saved.getId());
        return ResponseEntity.created(location).body(new CreateRes(saved.getId()));
    }

    // 목록
    @GetMapping
    public List<PostListItem> getPosts(@RequestParam(defaultValue = "FREE") String category) {
        return postService.listByCategory(category);
    }

    // 상세
    @GetMapping("/{id}")
    public PostDetail getPost(@PathVariable Long id) {
        return postService.detail(id);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication auth) {
        UserEntity me = userService.getCurrentUser(auth);
        postService.deletePost(id, me);
        return ResponseEntity.noContent().build();
    }

    // 수정
    @PatchMapping("/{id}")
    public PostDetail updatePost(
            @PathVariable Long id,
            @RequestBody UpdateReq req,
            Authentication auth
    ) {
        UserEntity user = userService.getCurrentUser(auth);
        return postService.update(id, req.title(), req.content(), req.category(), req.status(), user);
    }

    // ✅ 관리자 승인
    @PostMapping("/{id}/approve")
    public PostDetail approveSwap(@PathVariable Long id, Authentication auth) {
        UserEntity admin = userService.getCurrentUser(auth);
        return postService.approveSwap(id, admin);
    }

    // ✅ 관리자 거절
    @PostMapping("/{id}/reject")
    public PostDetail rejectSwap(@PathVariable Long id, Authentication auth) {
        UserEntity admin = userService.getCurrentUser(auth);
        return postService.rejectSwap(id, admin);
    }

    // ===== 요청/응답 DTO =====
    public record CreateReq(String category, String title, String content, String status) {}
    public record CreateRes(Long id) {}
    public record UpdateReq(String title, String content, String category, String status) {}
}