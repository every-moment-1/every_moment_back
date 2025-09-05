package com.rookies4.every_moment.common;

import java.time.OffsetDateTime;

public record BaseResponse<T>(T data, String timestamp) {
    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(data, OffsetDateTime.now().toString());
    }
}