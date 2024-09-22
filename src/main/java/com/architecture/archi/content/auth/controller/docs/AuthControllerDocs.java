package com.architecture.archi.content.auth.controller.docs;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.content.auth.model.AuthModel;
import com.architecture.archi.content.user.model.UserModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증/인가 API", description = "인증/인가 관련 컨트롤러입니다.")
public interface AuthControllerDocs {
    @Operation(summary = "로그인", description = "로그인 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "로그인 완료", content = @Content(schema = @Schema(implementation = AuthModel.AuthLoginRes.class)))
    })
    public ApiResponseModel<AuthModel.AuthLoginRes> login(@Valid @RequestBody AuthModel.AuthLoginReq request) throws CustomException;

    @Operation(summary = "소셜로그인", description = "소셜로그인 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "로그인 완료", content = @Content(schema = @Schema(implementation = AuthModel.AuthLoginRes.class)))
    })
    public ApiResponseModel<AuthModel.AuthLoginRes> oauthLogin(@Valid @RequestBody AuthModel.SnsAuthLoginReq request) throws CustomException;

    @Operation(summary = "로그아웃", description = "로그아웃 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "로그아웃 완료", content = @Content(schema = @Schema(implementation = AuthModel.AuthLoginRes.class)))
    })
    public ApiResponseModel<AuthModel.AuthLoginRes> logout(@RequestBody AuthModel.AuthLogoutReq request) throws CustomException;

    @Operation(summary = "리프레시", description = "리프레시 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리프레시 완료", content = @Content(schema = @Schema(implementation = AuthModel.AuthLoginRes.class)))
    })
    public ApiResponseModel<AuthModel.AuthLoginRes> refresh(HttpServletRequest request) throws CustomException;
}
