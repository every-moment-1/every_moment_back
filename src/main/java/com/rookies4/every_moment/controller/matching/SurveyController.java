package com.rookies4.every_moment.controller.matching;

import com.rookies4.every_moment.entity.matching.SurveyResult;
import com.rookies4.every_moment.service.matching.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    // 설문 제출
//    @PostMapping("/submit/{userId}")
//    public ResponseEntity<Void> submitSurvey(@PathVariable Long userId, @RequestBody SurveyResult surveyResult) {
//        if (userId == null) {
//            // userId가 없는 경우 Unauthorized(401) 반환
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        // 인증된 사용자 정보는 userId로 대체하여 서비스로 전달
//        surveyService.submitSurveyResult(userId, surveyResult);  // 설문 결과를 서비스로 전달
//        return ResponseEntity.status(HttpStatus.CREATED).build();  // 설문 제출 완료 응답
//    }

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


//설문 제출
//    @PostMapping("/submit/{userId}")
//    public ResponseEntity<Void> submitSurvey(@PathVariable Long userId, @RequestBody SurveyResult surveyResult) {
//        if (userId == null) {
//            // userId가 없는 경우 Unauthorized(401) 반환
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        // 인증된 사용자 정보는 userId로 대체하여 서비스로 전달
//        surveyService.submitSurveyResult(userId, surveyResult);  // 설문 결과를 서비스로 전달
//        return ResponseEntity.status(HttpStatus.CREATED).build();  // 설문 제출 완료 응답
//    }
//

//    @PreAuthorize("hasRole('USER')")
//    @PostMapping("/submit")
//    public ResponseEntity<Void> submitSurvey(Authentication authentication, @RequestBody SurveyResult surveyResult) {
//        if (authentication == null || authentication.getPrincipal() == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 인증되지 않음
//        }
//
//        UserEntity user = (UserEntity) authentication.getPrincipal(); // 인증된 사용자 가져오기
//        surveyService.submitSurveyResult(user.getId(), surveyResult);
//        return ResponseEntity.status(HttpStatus.CREATED).build();


    // 설문 결과 조회
    @GetMapping("/{userId}")
    public ResponseEntity<SurveyResult> getSurveyResult(@PathVariable Long userId) {
        SurveyResult surveyResult = surveyService.getSurveyResult(userId);
        return ResponseEntity.ok(surveyResult);
    }
}

