package com.rookies4.every_moment.controller.board;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.board.CommentEntity;
import com.rookies4.every_moment.service.UserService;
import com.rookies4.every_moment.service.board.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    // 댓글 작성
    @PostMapping("/{postId}")
    public CommentEntity addComment(@PathVariable Long postId, @RequestBody CommentEntity comment, Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);
        return commentService.addComment(postId, comment, user);
    }

    // 특정 게시글의 댓글 조회
    @GetMapping("/{postId}")
    public List<CommentEntity> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}