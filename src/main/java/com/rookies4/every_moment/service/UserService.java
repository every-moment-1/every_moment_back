package com.rookies4.every_moment.service;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public UserEntity getCurrentUser(Authentication auth) {
        String email = auth.getName(); // we set email as principal username
        return users.findByEmail(email).orElseThrow();
    }

    /** ✅ id로 사용자 조회 (없으면 IllegalArgumentException) */
    @Transactional(readOnly = true)
    public UserEntity getByIdOrThrow(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    /**
     * 사용자 이름(username) 수정
     */
    @Transactional
    public UserEntity updateUsername(UserEntity user, String newUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("username은 비어 있을 수 없습니다.");
        }
        // 변경 없음
        if (newUsername.equals(user.getUsername())) {
            return user;
        }
        // 중복 체크
        if (users.existsByUsername(newUsername)) {
            throw new IllegalStateException("이미 사용 중인 사용자명입니다.");
        }
        user.setUsername(newUsername);
        return users.save(user);
    }
}
