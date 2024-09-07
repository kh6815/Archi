package com.architecture.archi.config.security.user.oauth2;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.content.auth.model.AuthModel;
import com.architecture.archi.content.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ExceptionCode exceptionCode;

        // 예외 메시지를 확인하여 적절한 응답을 설정
        if (exception instanceof OAuth2AuthenticationException) {
            // OAuth2 인증 예외 발생 시 처리
            if (((OAuth2AuthenticationException) exception).getError().getErrorCode().equals(ExceptionCode.ALREADY_EXIST.getResultMessage())) {
                exceptionCode = ExceptionCode.ALREADY_EXIST;
            } else {
                exceptionCode = ExceptionCode.INTERNAL_SERVER_ERROR;
            }
        } else {
            exceptionCode = ExceptionCode.INTERNAL_SERVER_ERROR;
        }

        response.setStatus(exceptionCode.getStatusCode().value());
        response.setContentType("application/json");
        ApiResponseModel apiResponse = new ApiResponseModel(exceptionCode.getResultCode(), exceptionCode.getResultMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
