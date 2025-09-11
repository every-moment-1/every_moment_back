package com.rookies4.every_moment.board.controller;

import com.rookies4.every_moment.board.dto.CommentItem;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.service.UserService;
import com.rookies4.every_moment.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping("/{postId}")
    public Map<String, Long> addComment(@PathVariable Long postId,
                                        @RequestBody CreateReq req,
                                        Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);
        Long id = commentService.addComment(postId, req.content(), user);
        return Map.of("id", id);
    }

    @GetMapping("/{postId}")
    public List<CommentItem> getComments(@PathVariable Long postId) {
        return commentService.listByPost(postId);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

    @PatchMapping("/{id}")
    public CommentItem updateComment(@PathVariable Long id,
                                     @RequestBody UpdateReq req,
                                     Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);
        return commentService.updateComment(id, req.content(), user);
    }

    public record CreateReq(String content) {}
    public record UpdateReq(String content) {}
}