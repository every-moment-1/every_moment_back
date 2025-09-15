package com.rookies4.every_moment.controller;

import com.rookies4.every_moment.controller.dto.AuthDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.exception.BaseResponse;
import com.rookies4.every_moment.entity.dto.UserDTO;
import com.rookies4.every_moment.match.repository.MatchResultRepository;
import com.rookies4.every_moment.match.repository.SurveyResultRepository;
import com.rookies4.every_moment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/school")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final SurveyResultRepository surveyResultRepository; // [ADDED]
    private final MatchResultRepository matchResultRepository; // [ADDED]

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
    @GetMapping("/user/status")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getMyStatus(Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);
        Long uid = user.getId();

        // ✅ 설문 완료 여부: exists 사용 (빈 리스트/Optional 모두 안전)
        boolean surveyDone = surveyResultRepository.existsByUserId(uid);
        // 완료 플래그 컬럼이 있다면 아래 라인을 쓰세요:
        // boolean surveyDone = surveyResultRepository.existsByUserIdAndCompletedTrue(uid);

        // 매칭 상태
        String matchStatus = "none";
        var results = matchResultRepository.findByUserId(uid);
        if (results != null) {
            boolean hasAccepted = results.stream().anyMatch(r ->
                    r.getStatus() != null && "ACCEPTED".equalsIgnoreCase(r.getStatus().toString())
            );
            boolean hasPending = results.stream().anyMatch(r ->
                    r.getStatus() != null && "PENDING".equalsIgnoreCase(r.getStatus().toString())
            );
            if (hasAccepted) matchStatus = "done";
            else if (hasPending) matchStatus = "pending";
        }

        Map<String, Object> map = new HashMap<>();
        map.put("surveyDone", surveyDone);
        map.put("matchStatus", matchStatus);
        return ResponseEntity.ok(BaseResponse.ok(map));
    }
}
