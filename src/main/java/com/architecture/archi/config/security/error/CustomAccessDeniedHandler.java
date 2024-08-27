package com.architecture.archi.config.security.error;

import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.common.model.ApiResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증된 사용자가 권한이 없는 리소스에 접근할 때 호출
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ExceptionCode exceptionCode = ExceptionCode.FORBIDDEN;

//        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setStatus(exceptionCode.getStatusCode().value());
        response.setContentType("application/json");
        ApiResponseModel apiResponse = new ApiResponseModel(exceptionCode.getResultCode(), exceptionCode.getResultMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
