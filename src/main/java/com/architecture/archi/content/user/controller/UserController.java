package com.architecture.archi.content.user.controller;

import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.content.user.controller.Docs.UserControllerDocs;
import com.architecture.archi.content.user.model.UserModel;
import com.architecture.archi.content.user.service.UserWriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
@Tag(name = "유저 API", description = "유저 관련 컨트롤러입니다.")
public class UserController implements UserControllerDocs {

   private final UserWriteService userWriteService;

   @PostMapping("/signup")
   public ApiResponseModel<UserModel.UserSignUpRes> userSingUp(@Valid @RequestBody UserModel.UserSignUpReq userSignUpReq) {
       return new ApiResponseModel<>(UserModel.UserSignUpRes.builder()
               .id(userWriteService.createUser(userSignUpReq))
               .build());
   }
}
