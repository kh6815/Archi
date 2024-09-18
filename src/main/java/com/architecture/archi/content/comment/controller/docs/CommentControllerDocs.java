package com.architecture.archi.content.comment.controller.docs;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.comment.model.CommentModel;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "코멘트 API", description = "코멘트 관련 컨트롤러입니다.")
public interface CommentControllerDocs {

    @Operation(summary = "코멘트 리스트 조회", description = "코멘트 리스트 조회 API 입니다")
    @Parameters(value = {
            @Parameter(name = "contentId", description = "코멘트 리스트를 조회할 컨텐츠 아이디 입니다.", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "조회 완료", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ApiResponseModel<List<CommentModel.CommentDto>> getComments(@PathVariable("contentId") Long contentId, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    // 댓글 등록
    @Operation(summary = "코멘트 추가", description = "코멘트 추가 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "추가 완료", content = @Content(schema = @Schema(implementation = Long.class)))
    })
    public ApiResponseModel<Long> addComment(@Valid @RequestBody CommentModel.AddCommentReq addCommentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "코멘트 수정", description = "코멘트 수정 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "수정 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> updateComment(@Valid @RequestBody CommentModel.UpdateCommentReq updateCommentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "코멘트 삭제", description = "코멘트 삭제 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "조회 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> deleteComment(@RequestBody CommentModel.DeleteCommentReq deleteCommentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "코멘트 좋아요 클릭", description = "코멘트 좋아요 클릭 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "좋아요 적용 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> clickLike(@RequestBody CommentModel.UpdateCommentLikeReq updateCommentLikeReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;
}
