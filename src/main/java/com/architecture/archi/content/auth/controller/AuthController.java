package com.architecture.archi.content.auth.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.content.auth.controller.docs.AuthControllerDocs;
import com.architecture.archi.content.auth.model.AuthModel;
import com.architecture.archi.content.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    //TODO 카카오, 네이버 인증 로그인 만들기
    @PostMapping("/login")
    public ApiResponseModel<AuthModel.AuthLoginRes> login(@Valid @RequestBody AuthModel.AuthLoginReq request) throws CustomException {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public ApiResponseModel<AuthModel.AuthLoginRes> logout(@RequestBody AuthModel.AuthLoginReq request) throws CustomException {
        return authService.logout(request.getId());
    }

    @PostMapping("/refresh")
    public ApiResponseModel<AuthModel.AuthLoginRes> refresh(HttpServletRequest request) throws CustomException {
        return authService.refresh(request);
    }
}
