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

    @GetMapping
    public List<PostListItem> getPosts(@RequestParam(defaultValue = "FREE") String category) {
        return postService.listByCategory(category);
    }

    @GetMapping("/{id}")
    public PostDetail getPost(@PathVariable Long id) {
        return postService.detail(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public PostDetail updatePost(
            @PathVariable Long id,
            @RequestBody UpdateReq req,
            Authentication auth
    ) {
        UserEntity user = userService.getCurrentUser(auth);
        return postService.update(id, req.title(), req.content(), req.category(), user);
    }

    public record CreateReq(String category, String title, String content) {}
    public record CreateRes(Long id) {}
    public record UpdateReq(String title, String content, String category) {}
}