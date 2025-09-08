package com.rookies4.every_moment.controller;

import com.rookies4.every_moment.exception.BaseResponse;
import com.rookies4.every_moment.entity.dto.UserDTO;
import com.rookies4.every_moment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/school")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<BaseResponse<UserDTO>> me(Authentication auth) {
        var u = userService.getCurrentUser(auth);
        var res = new UserDTO(u.getId(), u.getUsername(), u.getGender(), u.getEmail(), u.getSmoking(), u.getRole(), u.getActive(), u.getCreatedAt().toString());
        return ResponseEntity.ok(BaseResponse.ok(res));
    }
}