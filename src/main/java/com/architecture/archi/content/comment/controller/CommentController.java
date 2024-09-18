package com.architecture.archi.content.comment.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.comment.controller.docs.CommentControllerDocs;
import com.architecture.archi.content.comment.model.CommentModel;
import com.architecture.archi.content.comment.service.CommentReadService;
import com.architecture.archi.content.comment.service.CommentWriteService;
import com.architecture.archi.content.content.model.ContentModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/comment")
public class CommentController implements CommentControllerDocs {

    private final CommentReadService commentReadService;
    private final CommentWriteService commentWriteService;

    // 조회 할때 작성자가 쓴 댓글인지 나타내주기
    @GetMapping("/list/{contentId}")
    public ApiResponseModel<List<CommentModel.CommentDto>> getComments(@PathVariable("contentId") Long contentId, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(commentReadService.findComments(contentId, userDetails));
    }

    // 댓글 등록
    @PostMapping("/add")
    public ApiResponseModel<Long> addComment(@Valid @RequestBody CommentModel.AddCommentReq addCommentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(commentWriteService.createComment(addCommentReq, userDetails));
    }

    @PatchMapping("/update")
    public ApiResponseModel<Boolean> updateComment(@Valid @RequestBody CommentModel.UpdateCommentReq updateCommentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(commentWriteService.updateComment(updateCommentReq, userDetails));
    }

    @DeleteMapping("/delete")
    public ApiResponseModel<Boolean> deleteComment(@RequestBody CommentModel.DeleteCommentReq deleteCommentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(commentWriteService.deleteComment(deleteCommentReq, userDetails));
    }

    @PostMapping("/like")
    public ApiResponseModel<Boolean> clickLike(@RequestBody CommentModel.UpdateCommentLikeReq updateCommentLikeReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(commentWriteService.updateLike(updateCommentLikeReq, userDetails));
    }

    // 내가 쓴 댓글 조회
    @GetMapping("/user/list/comment")
    public ApiResponseModel<Page<CommentModel.UserCommentDto>> getUserComments(@AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable) throws CustomException {
        return new ApiResponseModel<>(commentReadService.findUserComments(userDetails, pageable));
    }
}
