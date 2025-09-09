package com.rookies4.every_moment.service.board;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.board.CommentEntity;
import com.rookies4.every_moment.entity.board.PostEntity;
import com.rookies4.every_moment.repository.board.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    // 댓글 작성
    @Transactional
    public CommentEntity addComment(Long postId, CommentEntity comment, UserEntity author) {
        PostEntity post = postService.getPost(postId);
        comment.setPost(post);
        comment.setAuthor(author);
        return commentRepository.save(comment);
    }

    // 특정 게시글의 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentEntity> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long id) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + id));
        commentRepository.delete(comment);
    }
}