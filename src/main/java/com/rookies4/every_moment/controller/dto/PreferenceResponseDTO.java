package com.rookies4.every_moment.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreferenceResponseDTO {

    private Long id;
    private Long userId;  // userId를 바로 추가

    private Integer cleanliness;
    private Integer height;
    private Integer noiseSensitivity;
    private Integer roomTemp;
    private Integer sleepTime;
}