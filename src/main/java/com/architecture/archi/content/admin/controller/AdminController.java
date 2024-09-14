package com.architecture.archi.content.admin.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.admin.controller.docs.AdminControllerDocs;
import com.architecture.archi.content.admin.model.AdminModel;
import com.architecture.archi.content.admin.service.AdminReadService;
import com.architecture.archi.content.admin.service.AdminWriteService;
import com.architecture.archi.content.content.model.ContentModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin")
public class AdminController implements AdminControllerDocs {

    private final AdminReadService adminReadService;
    private final AdminWriteService adminWriteService;

    // 카테고리 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/category/add")
    public ApiResponseModel<Boolean> addCategory(@Valid @RequestBody AdminModel.AddCategoryReq addCategoryReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(adminWriteService.createCategory(addCategoryReq, userDetails));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/category/list")
    public ApiResponseModel<AdminModel.GetCategoryAdminRes> getAdminCategory() throws CustomException {
        try{
//            CategoryModel.GetCategoryAdminRes res = (CategoryModel.GetCategoryAdminRes) redisTemplate.opsForValue().get("adminCategories");
            List<AdminModel.CategoryAdminDto> categoryAdminDtoList = adminReadService.findAdminCategories();

            return new ApiResponseModel<>(AdminModel.GetCategoryAdminRes.builder()
                    .categoryList(categoryAdminDtoList)
                    .build());
        } catch(Exception e){
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, "서버 오류");
        }
    }

    //이름 변경 API
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/category/update/category-name")
    public ApiResponseModel<Boolean> updateCategoryName(@RequestBody AdminModel.UpdateCategoryReq updateCategoryReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(adminWriteService.updateCategoryName(updateCategoryReq.getId(), updateCategoryReq.getName(), userDetails));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/category/delete")
    public ApiResponseModel<Boolean> deleteCategory(@RequestParam("id") Long categoryId, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(adminWriteService.deleteCategory(categoryId, userDetails));
    }

    // 공지사항 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/notice/add")
    public ApiResponseModel<Long> addNotice(@Valid @RequestBody AdminModel.AddNoticeReq addNoticeReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(adminWriteService.createNotice(addNoticeReq, userDetails));
    }

    // 공지사항 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/notice/update")
    public ApiResponseModel<Boolean> updateNotice(@RequestBody AdminModel.UpdateNoticeReq updateNoticeReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(adminWriteService.updateNotice(updateNoticeReq, userDetails));
    }

    // 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/notice/delete")
    public ApiResponseModel<Boolean> deleteNotice(@RequestBody AdminModel.DeleteNoticeReq deleteNoticeReq) throws CustomException {
        return new ApiResponseModel<>(adminWriteService.deleteNotice(deleteNoticeReq));
    }
}
