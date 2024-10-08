package com.architecture.archi.content.user.controller.docs;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.user.model.UserModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저 API", description = "유저 관련 컨트롤러입니다.")
public interface UserControllerDocs {
    @Operation(summary = "회원가입", description = "유저 회원가입 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 완료", content = @Content(schema = @Schema(implementation = UserModel.UserSignUpRes.class)))
    })
    public ApiResponseModel<UserModel.UserSignUpRes> userSingUp(@Valid @RequestBody UserModel.UserSignUpReq userSignUpReq) throws CustomException;

    @Operation(summary = "아이디 체크", description = "유저 아이디체크 API 입니다")
    @Parameters(value = {
            @Parameter(name = "id", description = "체크할 id", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "체크 결과", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> checkId(@RequestParam("id") String id) throws CustomException;

    @Operation(summary = "이메일 체크", description = "유저 이메일체크 API 입니다")
    @Parameters(value = {
            @Parameter(name = "email", description = "체크할 email", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "체크 결과", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> checkEmail(@RequestParam("email") String email) throws CustomException;

    @Operation(summary = "닉네임 체크", description = "유저 닉네임체크 API 입니다")
    @Parameters(value = {
            @Parameter(name = "nickName", description = "체크할 nickName", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "체크 결과", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> checkNickName(@RequestParam("nickName") String nickName) throws CustomException;

    @Operation(summary = "비밀번호 초기화", description = "유저 비밀번호 초기화 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "비밀번호 초기화 완료", content = @Content(schema = @Schema(implementation = UserModel.InitPasswordRes.class)))
    })
    public ApiResponseModel<UserModel.InitPasswordRes> initPassword(@RequestBody UserModel.InitPasswordReq initPasswordReq) throws CustomException;

    @Operation(summary = "비밀번호 변경", description = "유저 비밀번호 변경 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "비밀번호 변경 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> changePassword(@RequestBody UserModel.ChangePasswordReq changePasswordReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "닉네임 변경", description = "유저 닉네임 변경 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "닉네임 변경 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> changeNickName(@RequestBody UserModel.ChangeNickNameReq changeNickNameReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    // 프로필 사진 변경
    @Operation(summary = "프로필 사진 변경", description = "유저 프로필 사진 변경 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "이미지 변경 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> changeImage(@Valid @RequestBody UserModel.ChangeImageReq changeImageReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    // 유저 심플 정보 조회
    @Operation(summary = "유저 심플 정보 조회", description = "유저 심플 정보 조회 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "조회 완료", content = @Content(schema = @Schema(implementation = UserModel.UserSimpleInfoRes.class)))
    })
    public ApiResponseModel<UserModel.UserSimpleInfoRes> getSimpleUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    // 유저 디테일 정보 조회
    @Operation(summary = "유저 디테일 정보 조회", description = "유저 디테일 정보 조회 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "조회 완료", content = @Content(schema = @Schema(implementation = UserModel.UserDetailInfoRes.class)))
    })
    public ApiResponseModel<UserModel.UserDetailInfoRes> getDetailUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    // 유저 삭제
    @Operation(summary = "유저 삭제", description = "유저 삭제 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "삭제 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;
}
