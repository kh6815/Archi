package com.architecture.archi.content.content.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.category.model.CategoryModel;
import com.architecture.archi.content.content.controller.docs.ContentControllerDocs;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.content.content.service.ContentReadService;
import com.architecture.archi.content.content.service.ContentWriteService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/content")
public class ContentController implements ContentControllerDocs {

    private final ContentReadService contentReadService;
    private final ContentWriteService contentWriteService;

    // 컨텐츠 리스트 조회 - page 적용
    @GetMapping("/list/{categoryId}")
    public ApiResponseModel<Page<ContentModel.ContentListDto>> getContents(@PathVariable("categoryId") Long categoryId, Pageable pageable) throws Exception {
        return new ApiResponseModel<>(contentReadService.findContents(categoryId, pageable));
    }

    // 컨텐츠 조회
    @GetMapping("/{id}")
    public ApiResponseModel<ContentModel.ContentDto> getContent(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(contentReadService.findContent(id, userDetails));
    }

    // 등록
    @PostMapping("/add")
    public ApiResponseModel<Long> addContent(@Valid @RequestBody ContentModel.AddContentReq addContentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(contentWriteService.createContent(addContentReq, userDetails));
    }

    // 수정
    @PatchMapping("/update")
    public ApiResponseModel<Boolean> updateContent(@RequestBody ContentModel.UpdateContentReq updateContentReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(contentWriteService.updateContent(userDetails, updateContentReq));
    }

    // 삭제
    @DeleteMapping("/delete")
    public ApiResponseModel<Boolean> deleteContent(@RequestBody ContentModel.DeleteContentReq deleteContentReq) throws CustomException {
        return new ApiResponseModel<>(contentWriteService.deleteContent(deleteContentReq));
    }

    // like 로직 만들기 -> 조회 할때나 삭제할때 like 필드 같이 지우기
    @PostMapping("/like")
    public ApiResponseModel<Boolean> clickLike(@RequestBody ContentModel.UpdateContentLikeReq updateContentLikeReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(contentWriteService.updateLike(updateContentLikeReq, userDetails));
    }

    //TODO admin에서 쓸 content 검색과 검색 필터를 만들기(유저 id, 제목, 등등)
}
