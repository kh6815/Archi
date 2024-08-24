package com.architecture.archi.content.user.controller.Docs;

import com.architecture.archi.common.model.ApiResponseModel;
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
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "유저 API", description = "유저 관련 컨트롤러입니다.")
public interface UserControllerDocs {
    @Operation(summary = "회원가입", description = "유저 회원가입 API 입니다")
//    @Parameters(value = {
//            @Parameter(name = "id", description = "회원가입할 유저 id", required = true),
//            @Parameter(name = "pw", description = "회원가입할 유저 pw", required = true),
//            @Parameter(name = "email", description = "회원가입할 유저 email", required = true),
//            @Parameter(name = "nickName", description = "회원가입할 유저 nickName", required = true),
//    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 완료", content = @Content(schema = @Schema(implementation = UserModel.UserSignUpRes.class)))
    })
    public ApiResponseModel<UserModel.UserSignUpRes> userSingUp(@Valid @RequestBody UserModel.UserSignUpReq userSignUpReq);
}
