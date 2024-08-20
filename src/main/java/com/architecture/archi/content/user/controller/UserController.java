package com.architecture.archi.content.user.controller;

import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.content.user.model.UserModel;
import com.architecture.archi.content.user.service.UserWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {

   private final UserWriteService userWriteService;

   @PostMapping("/signup")
   public ApiResponseModel<UserModel.UserSignUpRes> userSingUp(@Valid @RequestBody UserModel.UserSignUpReq userSignUpReq) {
       return new ApiResponseModel<>(UserModel.UserSignUpRes.builder()
               .id(userWriteService.createUser(userSignUpReq))
               .build());
   }
}
