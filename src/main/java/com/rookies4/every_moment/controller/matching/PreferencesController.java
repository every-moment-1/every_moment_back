package com.rookies4.every_moment.controller.matching;

import com.rookies4.every_moment.controller.dto.PreferenceResponseDTO;
import com.rookies4.every_moment.entity.matching.Preference;
import com.rookies4.every_moment.service.matching.PreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferencesController {

    private final PreferencesService preferencesService;

    // 선호도 계산 후 저장하는 API
    @PostMapping("/calculate/{userId}")
    public ResponseEntity<PreferenceResponseDTO> calculateAndSavePreferences(@PathVariable Long userId) {
        PreferenceResponseDTO responseDTO = preferencesService.calculateAndSavePreferences(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);  // 계산된 선호도를 응답으로 반환
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PreferenceResponseDTO> getPreferences(@PathVariable Long userId) {
        PreferenceResponseDTO preferenceDTO = preferencesService.getPreferences(userId);
        return ResponseEntity.ok(preferenceDTO);
    }
}