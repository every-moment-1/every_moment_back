package com.rookies4.every_moment.board.controller;

import com.rookies4.every_moment.board.dto.CommentItem;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.service.UserService;
import com.rookies4.every_moment.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // 댓글 작성: 요청 DTO(content) -> id만 반환
    @PostMapping("/{postId}")
    public Map<String, Long> addComment(@PathVariable Long postId,
                                        @RequestBody CreateReq req,
                                        Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);
        Long id = commentService.addComment(postId, req.content(), user);
        return Map.of("id", id);
    }

    // 특정 게시글의 댓글 조회
    @GetMapping("/{postId}")
    public List<CommentItem> getComments(@PathVariable Long postId) {
        return commentService.listByPost(postId);
    }

    // ✅ 댓글 삭제: 작성자 또는 관리자만
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication auth) {
        UserEntity me = userService.getCurrentUser(auth);
        commentService.deleteComment(id, me);  // ← actor 전달 (권한 체크 발동)
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 댓글 수정: 작성자 또는 관리자만
    @PatchMapping("/{id}")
    public CommentItem updateComment(@PathVariable Long id,
                                     @RequestBody UpdateReq req,
                                     Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);
        return commentService.updateComment(id, req.content(), user);
    }

    // 요청 DTO
    public record CreateReq(String content) {}
    public record UpdateReq(String content) {}
}