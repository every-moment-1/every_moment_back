package com.rookies4.every_moment.match.controller;

import com.rookies4.every_moment.match.controller.dto.SurveyResultResponseDTO;
import com.rookies4.every_moment.match.entity.SurveyResult;
import com.rookies4.every_moment.match.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    // 설문지 제출
    @PostMapping("/submit/{userId}")
    public ResponseEntity<SurveyResult> submitSurvey(@PathVariable Long userId, @RequestBody SurveyResult surveyResult) {
        if (userId == null) {
            // userId가 없는 경우 Unauthorized(401) 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 인증된 사용자 정보는 userId로 대체하여 서비스로 전달하고, 저장된 설문 결과를 받음
        SurveyResult savedSurveyResult = surveyService.submitSurveyResult(userId, surveyResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSurveyResult);   // 설문 제출 완료 응답
    }


    // 설문 결과 조회
    @GetMapping("/{userId}")
    public ResponseEntity<SurveyResultResponseDTO> getSurveyResult(@PathVariable Long userId) {
        SurveyResult surveyResult = surveyService.getSurveyResult(userId);

        // DTO 변환
        SurveyResultResponseDTO response = new SurveyResultResponseDTO(
                surveyResult.getId(),
                surveyResult.getUser().getId(), // user 정보 사용
                surveyResult.getSleepTime(),
                surveyResult.getCleanliness(),
                surveyResult.getNoiseSensitivity(),
                surveyResult.getHeight(),
                surveyResult.getRoomTemp()
        );

        return ResponseEntity.ok(response);
    }
}

