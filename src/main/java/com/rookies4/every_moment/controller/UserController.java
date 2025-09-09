package com.rookies4.every_moment.controller;

import com.rookies4.every_moment.entity.dto.UserDTO;
import com.rookies4.every_moment.exception.BaseResponse;
import com.rookies4.every_moment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/school")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<BaseResponse<UserDTO>> me(Authentication auth) {
        var u = userService.getCurrentUser(auth);

        // createdAt NPE 방지 (null-safe)
        String createdAtStr = Optional.ofNullable(u.getCreatedAt())
                .map(LocalDateTime::toString)
                .orElse(null);

        var res = new UserDTO(
                u.getId(),
                u.getUsername(),
                u.getGender(),
                u.getEmail(),
                u.getSmoking(),
                u.getRole(),
                u.getActive(),
                createdAtStr
        );
        return ResponseEntity.ok(BaseResponse.ok(res));
    }
}
