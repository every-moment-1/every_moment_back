package com.rookies4.every_moment.service.board;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.board.PostEntity;
import com.rookies4.every_moment.repository.board.PostRepository;
import com.rookies4.every_moment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    // 게시글 작성
    @Transactional
    public PostEntity createPost(PostEntity post, UserEntity author) {
        post.setAuthor(author);
        return postRepository.save(post);
    }

    // 게시글 목록 조회 (카테고리 기준)
    @Transactional(readOnly = true)
    public List<PostEntity> getPostsByCategory(String category) {
        return postRepository.findByCategory(category);
    }

    // 단일 게시글 조회
    @Transactional(readOnly = true)
    public PostEntity getPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다: " + id));
    }

    // 게시글 삭제 (Soft Delete)
    @Transactional
    public void deletePost(Long id) {
        PostEntity post = getPost(id);
        post.setDeleted(true);
        postRepository.save(post);
    }
}