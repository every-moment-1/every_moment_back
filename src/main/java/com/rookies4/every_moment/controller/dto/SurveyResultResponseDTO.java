// SurveyResultResponse.java
package com.rookies4.every_moment.controller.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SurveyResultResponseDTO {
    private Long id;
    private Long userId;
    private Integer sleepTime;
    private Integer cleanliness;
    private Integer noiseSensitivity;
    private Integer height;
    private Integer roomTemp;
}