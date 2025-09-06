package com.rookies4.every_moment.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rookies4.every_moment.exception.ApiErrorResponse;
import com.rookies4.every_moment.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final ObjectMapper om = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {
        var body = ApiErrorResponse.of(ErrorCode.ACCESS_DENIED, null, null);
        response.setStatus(ErrorCode.ACCESS_DENIED.status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        om.writeValue(response.getOutputStream(), body);
    }
}