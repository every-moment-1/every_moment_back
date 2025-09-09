package com.rookies4.every_moment.controller.board;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.board.PostEntity;
import com.rookies4.every_moment.service.UserService;
import com.rookies4.every_moment.service.board.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    // 게시글 작성
    @PostMapping
    public PostEntity createPost(@RequestBody PostEntity post, Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);
        return postService.createPost(post, user);
    }

    // 게시글 목록 조회
    @GetMapping
    public List<PostEntity> getPosts(@RequestParam(defaultValue = "FREE") String category) {
        return postService.getPostsByCategory(category);
    }

    // 게시글 단건 조회
    @GetMapping("/{id}")
    public PostEntity getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}