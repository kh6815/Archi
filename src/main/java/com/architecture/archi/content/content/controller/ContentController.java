package com.architecture.archi.content.content.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.admin.model.AdminModel;
import com.architecture.archi.content.content.controller.docs.ContentControllerDocs;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.content.content.service.ContentReadService;
import com.architecture.archi.content.content.service.ContentWriteService;
import com.architecture.archi.content.file.model.FileModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/content")
public class ContentController implements ContentControllerDocs {

    private final ContentReadService contentReadService;
    private final ContentWriteService contentWriteService;
    private final RedisTemplate<String, Object> redisTemplate;

    // 컨텐츠 리스트 조회 - page 적용
    @GetMapping("/list/{categoryId}")
    public ApiResponseModel<Page<ContentModel.ContentListDto>> getContents(@PathVariable("categoryId") Long categoryId, Pageable pageable) throws Exception {
        return new ApiResponseModel<>(contentReadService.findContents(categoryId, pageable));
    }

    // 인기 컨텐츠 조회
    @GetMapping("/list/popular")
    public ApiResponseModel<List<ContentModel.ContentListDto>> getPopularContent() throws CustomException {
        return new ApiResponseModel<>(contentReadService.findPopularContent());
    }

    // 컨텐츠 조회
    @GetMapping("/get/{id}")
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
        return new ApiResponseModel<>(contentWriteService.updateContent(updateContentReq, userDetails));
    }

//    @PatchMapping(value = "/update/{contentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ApiResponseModel<Boolean> updateContent(
//            @PathVariable Long contentId,
//            @RequestPart("data") @Valid ContentModel.UpdateContentReq updateContentReq,
//            @RequestPart(value = "addFileList", required = false) List<MultipartFile> addFileList,
//            @AuthenticationPrincipal CustomUserDetails userDetails
//    ) throws CustomException {
//        // 여기서 서비스 호출 및 업데이트 로직을 처리;
//        return new ApiResponseModel<>(contentWriteService.updateContent(contentId, updateContentReq, addFileList, userDetails));
//    }

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

    // 카테고리 리스트 조회
    @GetMapping("/list/category")
    public ApiResponseModel<AdminModel.GetCategoryRes> getCategory() throws CustomException {
        try{
            AdminModel.GetCategoryRes res = (AdminModel.GetCategoryRes) redisTemplate.opsForValue().get("categories");
            return new ApiResponseModel<>(res);
        } catch(Exception e){
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, "서버 오류");
        }
    }

    // 공지사항 리스트 조회
    @GetMapping("/list/notice")
    public ApiResponseModel<List<ContentModel.NoticeListDto>> getNotices() throws Exception {
        return new ApiResponseModel<>(contentReadService.findNotices());
    }

    // 공지사항 조회
    @GetMapping("/get/notice/{id}")
    public ApiResponseModel<ContentModel.NoticeDto> getNotice(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(contentReadService.findNotice(id, userDetails));
    }
}
