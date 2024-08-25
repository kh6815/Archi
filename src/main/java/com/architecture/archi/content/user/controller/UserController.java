package com.architecture.archi.content.user.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.content.user.controller.Docs.UserControllerDocs;
import com.architecture.archi.content.user.model.UserModel;
import com.architecture.archi.content.user.service.UserReadService;
import com.architecture.archi.content.user.service.UserWriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController implements UserControllerDocs {

   private final UserWriteService userWriteService;
   private final UserReadService userReadService;

   @PostMapping("/signup")
   public ApiResponseModel<UserModel.UserSignUpRes> userSingUp(@Valid @RequestBody UserModel.UserSignUpReq userSignUpReq) throws CustomException {
       return new ApiResponseModel<>(UserModel.UserSignUpRes.builder()
               .id(userWriteService.createUser(userSignUpReq))
               .build());
   }

   @GetMapping("/check-id")
   public ApiResponseModel<Boolean> checkId(
           @NotBlank(message = "필수값입니다.")
           @Size(min = 3, max = 20, message = "3글자이상 20글자이하입니다.")
           @RequestParam("id") String id
           ) throws CustomException {
       return new ApiResponseModel<>(userReadService.existCheckId(id));
   }

    @GetMapping("/check-nickname")
    public ApiResponseModel<Boolean> checkNickName(
            @NotBlank(message = "필수값입니다.")
            @Size(min = 2, max = 10, message = "2글자이상 10글자이하입니다.")
            @RequestParam("nickName") String nickName
    ) throws CustomException {
        return new ApiResponseModel<>(userReadService.existCheckNickName(nickName));
    }
}
