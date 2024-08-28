package com.architecture.archi.content.category.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.category.controller.docs.CategoryControllerDocs;
import com.architecture.archi.content.category.model.CategoryModel;
import com.architecture.archi.content.category.service.CategoryReadService;
import com.architecture.archi.content.category.service.CategoryWriteService;
import com.architecture.archi.content.user.model.UserModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/category")
public class CategoryController implements CategoryControllerDocs {

    private final CategoryReadService categoryReadService;
    private final CategoryWriteService categoryWriteService;
    private final RedisTemplate<String, Object> redisTemplate;

    // 카테고리 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/add")
    public ApiResponseModel<Boolean> addCategory(@Valid @RequestBody CategoryModel.AddCategoryReq addCategoryReq, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(categoryWriteService.createCategory(addCategoryReq, userDetails));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/list")
    public ApiResponseModel<CategoryModel.GetCategoryAdminRes> getAdminCategory() throws CustomException {
        try{
            CategoryModel.GetCategoryAdminRes res = (CategoryModel.GetCategoryAdminRes) redisTemplate.opsForValue().get("adminCategories");
            return new ApiResponseModel<>(res);
        } catch(Exception e){
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, "서버 오류");
        }
    }

    //이름 변경 API
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/update/category-name")
    public ApiResponseModel<Boolean> updateCategoryName(@RequestParam("id") Long categoryId, @RequestParam("name") String categoryName, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(categoryWriteService.updateCategoryName(categoryId, categoryName, userDetails));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete")
    public ApiResponseModel<Boolean> deleteCategory(@RequestParam("id") Long categoryId, @AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(categoryWriteService.deleteCategory(categoryId, userDetails));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/list")
    public ApiResponseModel<CategoryModel.GetCategoryRes> getCategory() throws CustomException {
        try{
            CategoryModel.GetCategoryRes res = (CategoryModel.GetCategoryRes) redisTemplate.opsForValue().get("categories");
            return new ApiResponseModel<>(res);
        } catch(Exception e){
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, "서버 오류");
        }
    }
}
