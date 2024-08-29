package com.architecture.archi.content.content.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.category.model.CategoryModel;
import com.architecture.archi.content.content.controller.docs.ContentControllerDocs;
import com.architecture.archi.content.content.service.ContentReadService;
import com.architecture.archi.content.content.service.ContentWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/content")
public class ContentController implements ContentControllerDocs {
    private final ContentReadService contentReadService;
    private final ContentWriteService contentWriteService;

    // 조회

    // 등록
    // 카테고리 등록
    @PostMapping("/add")
    public ApiResponseModel<Boolean> addCategory(@Valid @RequestBody  , @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(categoryWriteService.createCategory(addCategoryReq, userDetails));
    }

    // 수정

    // 삭제
}
