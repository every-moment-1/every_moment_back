package com.rookies4.every_moment.controller;

import com.rookies4.every_moment.controller.dto.AuthDTO;
import com.rookies4.every_moment.exception.BaseResponse;
import com.rookies4.every_moment.entity.dto.UserDTO;
import com.rookies4.every_moment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<BaseResponse<UserDTO>> me(Authentication auth) {
        var u = userService.getCurrentUser(auth);
        var res = new UserDTO(u.getId(), u.getUsername(), u.getGender(), u.getEmail(), u.getSmoking(), u.getRole(), u.getActive(), u.getCreatedAt().toString());
        return ResponseEntity.ok(BaseResponse.ok(res));
    }

    @PutMapping("/user")
    public ResponseEntity<BaseResponse<UserDTO>> updateUsername(
            Authentication auth,
            @RequestBody AuthDTO.UpdateUserRequest req) {

        var user = userService.getCurrentUser(auth);
        userService.updateUsername(user, req.username());
        var updated = new UserDTO(user.getId(), user.getUsername(), user.getGender(),
                user.getEmail(), user.getSmoking(),
                user.getRole(), user.getActive(),
                user.getCreatedAt().toString());
        return ResponseEntity.ok(BaseResponse.ok(updated));
    }

}