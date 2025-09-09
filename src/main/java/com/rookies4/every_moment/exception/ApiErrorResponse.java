package com.rookies4.every_moment.exception;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApiErrorResponse(
        String error,
        String code,
        String message,
        String field,
        String timestamp,
        String traceId
) {
    public static ApiErrorResponse of(ErrorCode ec, String field, String messageOverride) {
        return new ApiErrorResponse(
                ec.name(),
                ec.code,
                messageOverride != null ? messageOverride : ec.message,
                field,
                OffsetDateTime.now().toString(),
                UUID.randomUUID().toString()
        );
    }
}