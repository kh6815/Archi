package com.architecture.archi.content.admin.service;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.content.admin.model.AdminModel;
import com.architecture.archi.db.entity.category.CategoryEntity;
import com.architecture.archi.db.repository.category.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminReadService {

    private final CategoryRepository categoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 초기화 시점에 레디스에 데이터 로드
    @PostConstruct
    public void init()  {
        List<CategoryEntity> categoryEntities = findCategories();

//        List<CategoryModel.CategoryAdminDto> categoryAdminDtoList = new ArrayList<>();
        List<AdminModel.CategoryDto> categoryDtoList = new ArrayList<>();

        for(CategoryEntity categoryEntity : categoryEntities){
            if(categoryEntity.getParentsCategory() == null && categoryEntity.getActiveYn().equals(BooleanFlag.Y)){ // 최상단 노드
//                CategoryModel.CategoryAdminDto admindto = CategoryModel.CategoryAdminDto.builder()
//                        .id(categoryEntity.getId())
//                        .categoryName(categoryEntity.getCategoryName())
//                        .createUser(categoryEntity.getCreateUser())
//                        .updateUser(categoryEntity.getUpdateUser())
//                        .activeYn(categoryEntity.getActiveYn())
//                        .subCategories(getAdminSubCategory(categoryEntity.getId(), categoryEntities))
//                        .build();

                AdminModel.CategoryDto dto = AdminModel.CategoryDto.builder()
                        .id(categoryEntity.getId())
                        .categoryName(categoryEntity.getCategoryName())
                        .subCategories(getSubCategory(categoryEntity.getId(), categoryEntities))
                        .build();

//                categoryAdminDtoList.add(admindto);
                categoryDtoList.add(dto);
            }
        }

//        redisTemplate.opsForValue().set("adminCategories", CategoryModel.GetCategoryAdminRes.builder()
//                        .categoryList(categoryAdminDtoList)
//                        .build());
        redisTemplate.opsForValue().set("categories", AdminModel.GetCategoryRes.builder()
                        .categoryList(categoryDtoList)
                        .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CategoryEntity> findCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AdminModel.CategoryAdminDto> findAdminCategories() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();

        List<AdminModel.CategoryAdminDto> categoryAdminDtoList = new ArrayList<>();

        for(CategoryEntity categoryEntity : categoryEntities){
            if(categoryEntity.getParentsCategory() == null && categoryEntity.getActiveYn().equals(BooleanFlag.Y)){ // 최상단 노드
                AdminModel.CategoryAdminDto admindto = AdminModel.CategoryAdminDto.builder()
                        .id(categoryEntity.getId())
                        .categoryName(categoryEntity.getCategoryName())
                        .createUser(categoryEntity.getCreateUser())
                        .updateUser(categoryEntity.getUpdateUser())
                        .activeYn(categoryEntity.getActiveYn())
                        .subCategories(getAdminSubCategory(categoryEntity.getId(), categoryEntities))
                        .build();

                categoryAdminDtoList.add(admindto);
            }
        }

        return categoryAdminDtoList;
    }

    // 재귀 함수
    private List<AdminModel.CategoryAdminDto> getAdminSubCategory(Long parentId, List<CategoryEntity> categoryEntities){
        List<AdminModel.CategoryAdminDto> subAdminCategories = new ArrayList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {
            if (categoryEntity.getParentsCategory() != null
                    && categoryEntity.getParentsCategory().getId().equals(parentId)
                    && categoryEntity.getActiveYn().equals(BooleanFlag.Y)) {
                AdminModel.CategoryAdminDto dto = AdminModel.CategoryAdminDto.builder()
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

    private List<AdminModel.CategoryDto> getSubCategory(Long parentId, List<CategoryEntity> categoryEntities){
        List<AdminModel.CategoryDto> subCategories = new ArrayList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {
            if (categoryEntity.getParentsCategory() != null && categoryEntity.getParentsCategory().getId().equals(parentId)) {
                if(categoryEntity.getActiveYn().equals(BooleanFlag.N)){
                    continue;
                }

                AdminModel.CategoryDto dto = AdminModel.CategoryDto.builder()
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
