package com.architecture.archi.content.category.model;

import com.architecture.archi.common.enumobj.BooleanFlag;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class CategoryModel {
    /*
    * request
    * */
    @Getter
    public static class AddCategoryReq{
        private Long parentsId; // 최상단 노드일 경우 0
        @NotBlank(message = "필수값입니다.")
        private String categoryName;
    }

    /*
    * response
    * */

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CategoryAdminDto{
        private Long id;
        private String categoryName;
        private BooleanFlag activeYn;
        private String createUser;
        private String updateUser;
        private List<CategoryAdminDto> subCategories;

        @Builder
        public CategoryAdminDto(Long id, String categoryName, List<CategoryAdminDto> subCategories, BooleanFlag activeYn, String createUser, String updateUser) {
            this.id = id;
            this.categoryName = categoryName;
            this.subCategories = subCategories;
            this.activeYn = activeYn;
            this.createUser = createUser;
            this.updateUser = updateUser;
        }
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetCategoryAdminRes{
        private List<CategoryAdminDto> categoryList;

        @Builder
        public GetCategoryAdminRes(List<CategoryAdminDto> categoryList) {
            this.categoryList = categoryList;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CategoryDto{
        private Long id;
        private String categoryName;
        private List<CategoryDto> subCategories;

        @Builder
        public CategoryDto(Long id, String categoryName, List<CategoryDto> subCategories) {
            this.id = id;
            this.categoryName = categoryName;
            this.subCategories = subCategories;
        }
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetCategoryRes{
        private List<CategoryDto> categoryList;

        @Builder
        public GetCategoryRes(List<CategoryDto> categoryList) {
            this.categoryList = categoryList;
        }
    }
}
