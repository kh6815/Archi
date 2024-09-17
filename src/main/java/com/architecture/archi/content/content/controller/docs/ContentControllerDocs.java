package com.architecture.archi.content.content.controller.docs;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.admin.model.AdminModel;
import com.architecture.archi.content.content.model.ContentModel;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "콘텐츠 API", description = "콘텐츠 관련 컨트롤러입니다.")
public interface ContentControllerDocs {
    @Operation(summary = "컨텐츠 리스트 조회 - page 적용", description = "컨텐츠 리스트 조회 API 입니다")
    @Parameters(value = {
            @Parameter(name = "categoryId", description = "조회할 컨텐츠 카테고리 입니다. (0 -> 전체 카테고리 컨텐츠 조회)", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "조회 완료", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ApiResponseModel<Page<ContentModel.ContentListDto>> getContents(@PathVariable("categoryId") Long categoryId, Pageable pageable) throws Exception;

    @Operation(summary = "컨텐츠 조회", description = "컨텐츠 조회 API 입니다")
    @Parameters(value = {
            @Parameter(name = "id", description = "조회할 컨텐츠 아이디 입니다.", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "조회 완료", content = @Content(schema = @Schema(implementation = ContentModel.ContentDto.class)))
    })
    public ApiResponseModel<ContentModel.ContentDto> getContent(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "컨텐츠 좋아요 클릭", description = "컨텐츠 좋아요 클릭 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "좋아요 적용 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> clickLike(@RequestBody ContentModel.UpdateContentLikeReq updateContentLikeReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "컨텐츠 등록", description = "컨텐츠 등록 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "컨텐츠 등록 완료", content = @Content(schema = @Schema(implementation = Long.class)))
    })
    public ApiResponseModel<Long> addContent(@Valid @RequestBody ContentModel.AddContentReq addContentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

//    @Operation(summary = "컨텐츠 수정", description = "컨텐츠 수정 API 입니다")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "컨텐츠 수정 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
//    })
//    public ApiResponseModel<Boolean> updateContent(@RequestBody ContentModel.UpdateContentReq updateContentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "컨텐츠 삭제", description = "컨텐츠 삭제 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "컨텐츠 삭제 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> deleteContent(@RequestBody ContentModel.DeleteContentReq deleteContentReq) throws CustomException;

    @Operation(summary = "카테고리 조회", description = "카테고리 조회 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 조회 완료", content = @Content(schema = @Schema(implementation = AdminModel.GetCategoryRes.class)))
    })
    public ApiResponseModel<AdminModel.GetCategoryRes> getCategory() throws CustomException;

    @Operation(summary = "공지사항 리스트 조회", description = "공지사항 리스트 조회 API 입니다")
    @Parameters(value = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "조회 완료", content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ApiResponseModel<List<ContentModel.NoticeListDto>> getNotices() throws Exception;

    // 공지사항 조회
    @Operation(summary = "공지사항 조회", description = "공지사항 조회 API 입니다")
    @Parameters(value = {
            @Parameter(name = "id", description = "조회할 공지사항 아이디 입니다.", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "조회 완료", content = @Content(schema = @Schema(implementation = ContentModel.NoticeDto.class)))
    })
    public ApiResponseModel<ContentModel.NoticeDto> getNotice(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;
}
