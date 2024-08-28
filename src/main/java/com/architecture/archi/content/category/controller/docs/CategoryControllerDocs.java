package com.architecture.archi.content.category.controller.docs;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.category.model.CategoryModel;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "카테고리 API", description = "카테고리 관련 컨트롤러입니다.")
public interface CategoryControllerDocs {
    @Operation(summary = "카테고리 등록", description = "카테고리 등록 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 등록 완료", content = @Content(schema = @Schema(implementation = CategoryModel.AddCategoryReq.class)))
    })
    public ApiResponseModel<Boolean> addCategory(@Valid @RequestBody CategoryModel.AddCategoryReq addCategoryReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "카테고리 어드민 조회", description = "카테고리 어드민 조회 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 어드민 조회 완료", content = @Content(schema = @Schema(implementation = CategoryModel.GetCategoryAdminRes.class)))
    })
    public ApiResponseModel<CategoryModel.GetCategoryAdminRes> getAdminCategory() throws CustomException;

    @Operation(summary = "카테고리 조회", description = "카테고리 조회 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 조회 완료", content = @Content(schema = @Schema(implementation = CategoryModel.GetCategoryRes.class)))
    })
    public ApiResponseModel<CategoryModel.GetCategoryRes> getCategory() throws CustomException;

    @Operation(summary = "카테고리 이름 변경", description = "카테고리 이름 변경 API 입니다")
    @Parameters(value = {
            @Parameter(name = "id", description = "카테고리 아이디", required = true),
            @Parameter(name = "name", description = "카테고리 이름", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 이름 변경 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> updateCategoryName(@RequestParam("id") Long categoryId, @RequestParam("name") String categoryName, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "카테고리 비활성", description = "카테고리 비활성 API 입니다")
    @Parameters(value = {
            @Parameter(name = "id", description = "카테고리 아이디", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 비활성 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> deleteCategory(@RequestParam("id") Long categoryId, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;
}
