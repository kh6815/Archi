package com.architecture.archi.content.category.service;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.content.category.model.CategoryModel;
import com.architecture.archi.db.entity.category.CategoryEntity;
import com.architecture.archi.db.repository.category.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryReadService {

    private final CategoryRepository categoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 초기화 시점에 레디스에 데이터 로드
    @PostConstruct
    public void init()  {
        List<CategoryEntity> categoryEntities = findCategories();

        List<CategoryModel.CategoryAdminDto> categoryAdminDtoList = new ArrayList<>();
        List<CategoryModel.CategoryDto> categoryDtoList = new ArrayList<>();

        for(CategoryEntity categoryEntity : categoryEntities){
            if(categoryEntity.getParentsCategory() == null){ // 최상단 노드
                CategoryModel.CategoryAdminDto admindto = CategoryModel.CategoryAdminDto.builder()
                        .id(categoryEntity.getId())
                        .categoryName(categoryEntity.getCategoryName())
                        .createUser(categoryEntity.getCreateUser())
                        .updateUser(categoryEntity.getUpdateUser())
                        .activeYn(categoryEntity.getActiveYn())
                        .subCategories(getAdminSubCategory(categoryEntity.getId(), categoryEntities))
                        .build();

                CategoryModel.CategoryDto dto = CategoryModel.CategoryDto.builder()
                        .id(categoryEntity.getId())
                        .categoryName(categoryEntity.getCategoryName())
                        .subCategories(getSubCategory(categoryEntity.getId(), categoryEntities))
                        .build();

                categoryAdminDtoList.add(admindto);
                categoryDtoList.add(dto);
            }
        }

        redisTemplate.opsForValue().set("adminCategories", CategoryModel.GetCategoryAdminRes.builder()
                        .categoryList(categoryAdminDtoList)
                        .build());
        redisTemplate.opsForValue().set("categories", CategoryModel.GetCategoryRes.builder()
                        .categoryList(categoryDtoList)
                        .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CategoryEntity> findCategories() {
        return categoryRepository.findAll();
    }

    // 재귀 함수
    private List<CategoryModel.CategoryAdminDto> getAdminSubCategory(Long parentId, List<CategoryEntity> categoryEntities){
        List<CategoryModel.CategoryAdminDto> subAdminCategories = new ArrayList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {
            if (categoryEntity.getParentsCategory() != null && categoryEntity.getParentsCategory().getId().equals(parentId)) {
                CategoryModel.CategoryAdminDto dto = CategoryModel.CategoryAdminDto.builder()
                        .id(categoryEntity.getId())
                        .categoryName(categoryEntity.getCategoryName())
                        .createUser(categoryEntity.getCreateUser())
                        .updateUser(categoryEntity.getUpdateUser())
                        .activeYn(categoryEntity.getActiveYn())
                        .subCategories(getAdminSubCategory(categoryEntity.getId(), categoryEntities))
                        .build();

                subAdminCategories.add(dto);
            }
        }
        return subAdminCategories;
    }

    private List<CategoryModel.CategoryDto> getSubCategory(Long parentId, List<CategoryEntity> categoryEntities){
        List<CategoryModel.CategoryDto> subCategories = new ArrayList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {
            if (categoryEntity.getParentsCategory() != null && categoryEntity.getParentsCategory().getId().equals(parentId)) {
                if(categoryEntity.getActiveYn().equals(BooleanFlag.N)){
                    continue;
                }

                CategoryModel.CategoryDto dto = CategoryModel.CategoryDto.builder()
                        .id(categoryEntity.getId())
                        .categoryName(categoryEntity.getCategoryName())
                        .subCategories(getSubCategory(categoryEntity.getId(), categoryEntities))
                        .build();

                subCategories.add(dto);
            }
        }
        return subCategories;
    }
}
