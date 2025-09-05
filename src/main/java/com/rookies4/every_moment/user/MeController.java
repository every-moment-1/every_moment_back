package com.rookies4.every_moment.user;

import com.rookies4.every_moment.common.BaseResponse;
import com.rookies4.every_moment.user.dto.MeResponse;
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

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<MeResponse>> me(Authentication auth) {
        var u = userService.getCurrentUser(auth);
        var res = new MeResponse(u.getId(), u.getUsername(), u.getEmail(), u.getSmoking(), u.getRole(), u.getActive(), u.getCreatedAt().toString());
        return ResponseEntity.ok(BaseResponse.ok(res));
    }
}