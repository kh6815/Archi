package com.architecture.archi.content.category.service;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.category.model.CategoryModel;
import com.architecture.archi.db.entity.category.CategoryEntity;
import com.architecture.archi.db.repository.category.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryWriteService {

    private final CategoryRepository categoryRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(rollbackFor = Exception.class)
    public Boolean createCategory(CategoryModel.AddCategoryReq addCategoryReq, CustomUserDetails userDetails) throws CustomException {
        // 등록시 주의할점 addCategoryReq.parentsId 0인 경우는 부모카테고리를 가져올 필요 없음

        CategoryEntity parentsCategory = null;

        if(addCategoryReq.getParentsId() != 0){
            parentsCategory = categoryRepository.findById(addCategoryReq.getParentsId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않은 상단 카테고리입니다."));

            if(parentsCategory.getActiveYn().equals(BooleanFlag.N)){
                throw new CustomException(ExceptionCode.NOT_EXIST, "활성화되지 않은 카테고리입니다.");
            }
        }

        CategoryEntity categoryEntity = CategoryEntity.builder()
                .parentsCategory(parentsCategory)
                .categoryName(addCategoryReq.getCategoryName())
                .createUser(userDetails.getUsername())
                .build();

        categoryRepository.save(categoryEntity);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategoryName(Long categoryId, String categoryName, CustomUserDetails userDetails) throws CustomException {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 카테고리"));

        categoryEntity.updateCategoryName(categoryName, userDetails.getUsername());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCategory(Long categoryId, CustomUserDetails userDetails) throws CustomException {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 카테고리"));

        categoryEntity.updateActive(BooleanFlag.N, userDetails.getUsername());
        return true;
    }
}
