package com.rookies4.every_moment.match.controller;

import com.rookies4.every_moment.match.controller.dto.PreferenceResponseDTO;
import com.rookies4.every_moment.match.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferencesService;

    // 선호도 계산 후 저장하는 API
    @PostMapping("/calculate/{userId}")
    public ResponseEntity<PreferenceResponseDTO> calculateAndSavePreferences(@PathVariable Long userId) {
        PreferenceResponseDTO responseDTO = preferencesService.calculateAndSavePreferences(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);  // 계산된 선호도를 응답으로 반환
    }
}