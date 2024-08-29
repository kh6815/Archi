package com.architecture.archi.content.content.model;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.content.category.model.CategoryModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class ContentModel {
    /*
    * request
    * */
    @Getter
    public static class AddContentReq{
        @NotNull(message = "필수값입니다.")
        private Long categoryId;

        @NotBlank(message = "필수값입니다.")
        private String title;

        @NotBlank(message = "필수값입니다.")
        private String content;
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
        private List<CategoryModel.CategoryAdminDto> subCategories;

        @Builder
        public CategoryAdminDto(Long id, String categoryName, List<CategoryModel.CategoryAdminDto> subCategories, BooleanFlag activeYn, String createUser, String updateUser) {
            this.id = id;
            this.categoryName = categoryName;
            this.subCategories = subCategories;
            this.activeYn = activeYn;
            this.createUser = createUser;
            this.updateUser = updateUser;
        }
    }
}
