package com.architecture.archi.content.user.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.user.controller.docs.UserControllerDocs;
import com.architecture.archi.content.user.model.UserModel;
import com.architecture.archi.content.user.service.UserReadService;
import com.architecture.archi.content.user.service.UserWriteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController implements UserControllerDocs {

   private final UserWriteService userWriteService;
   private final UserReadService userReadService;

   // 회원가입
   @PostMapping("/signup")
   public ApiResponseModel<UserModel.UserSignUpRes> userSingUp(@Valid @RequestBody UserModel.UserSignUpReq userSignUpReq) throws CustomException {
       return new ApiResponseModel<>(UserModel.UserSignUpRes.builder()
               .id(userWriteService.createUser(userSignUpReq))
               .build());
   }

   // 아이디 체크
   @GetMapping("/check-id")
   public ApiResponseModel<Boolean> checkId(
           @NotBlank(message = "필수값입니다.")
           @Size(min = 3, max = 20, message = "3글자이상 20글자이하입니다.")
           @RequestParam("id") String id
           ) throws CustomException {
       return new ApiResponseModel<>(userReadService.existCheckId(id));
   }

    // 이메일 체크
    @GetMapping("/check-email")
    public ApiResponseModel<Boolean> checkEmail(
            @NotBlank(message = "필수값입니다.")
            @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "형식이 올바르지 않습니다.")
            @RequestParam("email") String email
    ) throws CustomException {
        return new ApiResponseModel<>(userReadService.existCheckEmail(email));
    }

    // 닉네임 체크
    @GetMapping("/check-nickname")
    public ApiResponseModel<Boolean> checkNickName(
            @NotBlank(message = "필수값입니다.")
            @Size(min = 2, max = 10, message = "2글자이상 10글자이하입니다.")
            @RequestParam("nickName") String nickName
    ) throws CustomException {
        return new ApiResponseModel<>(userReadService.existCheckNickName(nickName));
    }

    // 비밀번호 초기화
    @PatchMapping("/init-password")
    public ApiResponseModel<UserModel.InitPasswordRes> initPassword(@RequestBody UserModel.InitPasswordReq initPasswordReq) throws CustomException {
        return new ApiResponseModel<>(userWriteService.initPassword(initPasswordReq.getId()));
    }

    // 비밀번호 변경(현재 비밀번호, 바꿀 비밀번호, 확인 비밀번호)
//    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/change-password")
    public ApiResponseModel<Boolean> changePassword(@Valid @RequestBody UserModel.ChangePasswordReq changePasswordReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(userWriteService.changePassword(changePasswordReq, userDetails));
    }

    // 닉네임 변경
//    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/change-nickname")
    public ApiResponseModel<Boolean> changeNickName(@Valid @RequestBody UserModel.ChangeNickNameReq changeNickNameReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
       return new ApiResponseModel<>(userWriteService.changeNickName(changeNickNameReq, userDetails));
    }

    // 프로필 사진 변경
    @PatchMapping("/change-image")
    public ApiResponseModel<Boolean> changeImage(@Valid @RequestBody UserModel.ChangeImageReq changeImageReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(userWriteService.changeImage(changeImageReq, userDetails));
    }

    // 유저 심플 정보 조회
    @GetMapping
    public ApiResponseModel<UserModel.UserSimpleInfoRes> getSimpleUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
       return new ApiResponseModel<>(userReadService.findUserSimpleInfo(userDetails));
    }

    // 유저 디테일 정보 조회
    @GetMapping("/detail")
    public ApiResponseModel<UserModel.UserDetailInfoRes> getDetailUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(userReadService.findUserDetailInfo(userDetails));
    }

    // 유저 삭제
    @DeleteMapping
    public ApiResponseModel<Boolean> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
       return new ApiResponseModel<>(userWriteService.deleteUser(userDetails));
    }
}
