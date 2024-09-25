package com.architecture.archi.content.content.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.admin.model.AdminModel;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.db.entity.content.BestContentEntity;
import com.architecture.archi.db.entity.content.ContentEntity;
import com.architecture.archi.db.entity.content.ContentFileEntity;
import com.architecture.archi.db.entity.like.ContentLikeEntity;
import com.architecture.archi.db.entity.notice.NoticeEntity;
import com.architecture.archi.db.entity.user.UserFileEntity;
import com.architecture.archi.db.repository.content.ContentDao;
import com.architecture.archi.db.repository.content.ContentRepository;
import com.architecture.archi.db.repository.file.FileDao;
import com.architecture.archi.db.repository.user.UserDao;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ContentReadService {

    private final ContentRepository contentRepository;
    private final ContentDao contentDao;
    private final FileDao fileDao;
    private final UserDao userDao;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public ContentModel.ContentDto findContent(Long id, CustomUserDetails userDetails) throws CustomException {
        // 컨텐츠와 연관된 user, category를 모두 가져오기
        ContentEntity contentEntity = contentDao.findContent(id);

        String contentAuthorImgUrl = null;
        Optional<UserFileEntity> userFileEntityOptional = userDao.findUserFileWithFileByUserId(contentEntity.getUser().getId());

        if(userFileEntityOptional.isPresent()){
            contentAuthorImgUrl = userFileEntityOptional.get().getFile().getUrl();
        }

        boolean isAvailableUpdate = false;

        if(userDetails != null){
            isAvailableUpdate = contentEntity.getUser().getId().equals(userDetails.getUsername());
        }

        ContentModel.ContentDto response = ContentModel.ContentDto.builder()
                .id(contentEntity.getId())
                .title(contentEntity.getTitle())
                .content(contentEntity.getContent())
                .categoryName(contentEntity.getCategory().getCategoryName())
                .isAvailableUpdate(isAvailableUpdate)
                .updatedAt(contentEntity.getUpdatedAt())
                .like((long) contentEntity.getContentLikes().size())
                .contentAuthorNickName(contentEntity.getUser().getNickName())
                .contentAuthorImgUrl(contentAuthorImgUrl)
                .build();

        List<Long> contentLikeIds = contentEntity.getContentLikes().stream()
                .map(ContentLikeEntity::getId).toList();

        List<String> likeUserIdList = contentDao.findLikeUserIdListByContentLikeIds(contentLikeIds);

        response.setLikeUserIds(likeUserIdList);
        response.setFileList(fileDao.findFilesByContentId(id));

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ContentModel.ContentListDto> findPopularContent() throws CustomException {
        List<ContentModel.ContentListDto> bestContentList = new ArrayList<>();

        List<BestContentEntity> bestContentEntityList = contentDao.findPopularContent();

        List<Long> contentIdList = new ArrayList<>();
        for (BestContentEntity bestContentEntity : bestContentEntityList) {
            ContentEntity contentEntity = bestContentEntity.getContent();

            bestContentList.add(ContentModel.ContentListDto.builder()
                            .id(contentEntity.getId())
                            .categoryName(contentEntity.getCategory().getCategoryName())
                            .title(contentEntity.getTitle())
                            .content(contentEntity.getContent())
                            .updatedAt(contentEntity.getUpdatedAt())
                            .like((long) contentEntity.getContentLikes().size())
                    .build());

            contentIdList.add(contentEntity.getId());
        }

        Map<Long, ContentFileEntity> contentFileEntityMap = contentDao.findSingleContentFileByContentIds(contentIdList);

        for (ContentModel.ContentListDto contentListDto : bestContentList) {
            ContentFileEntity contentFileEntity = contentFileEntityMap.get(contentListDto.getId());
            if(contentFileEntity != null) {
                contentListDto.setImgUrl(contentFileEntity.getFile().getUrl());
            }
        }

        return bestContentList;
    }

    @Transactional(rollbackFor = Exception.class)
    public Page<ContentModel.ContentListDto> findContents(Long categoryId, Pageable pageable) throws CustomException {
        List<Long> categoryIds = new ArrayList<>();

        if(categoryId != 0){
            AdminModel.GetCategoryRes categoryRes = (AdminModel.GetCategoryRes) redisTemplate.opsForValue().get("categories");

            if(categoryRes != null){
                AdminModel.CategoryDto findCategory = findCategoryIds(categoryId, categoryRes.getCategoryList());

                if(findCategory != null){
                    categoryIds.add(findCategory.getId());
                    findCategoryWithSubCategoryIds(findCategory, categoryIds);
                }
            }
        }

        return contentDao.findContentPages(categoryId, pageable, categoryIds);
    }

    private AdminModel.CategoryDto findCategoryIds(Long selectCategoryId, List<AdminModel.CategoryDto> categories){
        AdminModel.CategoryDto result = null;
        for (AdminModel.CategoryDto category : categories) {
            if(selectCategoryId == category.getId()){
                result = category;
                return result;
            }

            if(!category.getSubCategories().isEmpty()) {
                result = findCategoryIds(selectCategoryId, category.getSubCategories());
            }
        }
        return result;
    }

    private void findCategoryWithSubCategoryIds(AdminModel.CategoryDto category, List<Long> selectCategoryList){
        if(!category.getSubCategories().isEmpty()){
            for (AdminModel.CategoryDto subCategory : category.getSubCategories()) {
                selectCategoryList.add(subCategory.getId());

                if(!subCategory.getSubCategories().isEmpty()){
                    findCategoryWithSubCategoryIds(subCategory, selectCategoryList);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ContentModel.NoticeDto findNotice(Long id, CustomUserDetails userDetails) throws CustomException {
        NoticeEntity noticeEntity = contentDao.findNotice(id);

        String noticeAuthorImgUrl = null;
        Optional<UserFileEntity> userFileEntityOptional = userDao.findUserFileWithFileByUserId(noticeEntity.getUser().getId());

        if(userFileEntityOptional.isPresent()){
            noticeAuthorImgUrl = userFileEntityOptional.get().getFile().getUrl();
        }

        boolean isAvailableUpdate = false;

        if(userDetails != null){
            isAvailableUpdate = noticeEntity.getUser().getId().equals(userDetails.getUsername());
        }

        ContentModel.NoticeDto response = ContentModel.NoticeDto.builder()
                .id(noticeEntity.getId())
                .title(noticeEntity.getTitle())
                .content(noticeEntity.getContent())
                .isAvailableUpdate(isAvailableUpdate)
                .updatedAt(noticeEntity.getUpdatedAt())
                .noticeAuthorNickName(noticeEntity.getUser().getNickName())
                .noticeAuthorImgUrl(noticeAuthorImgUrl)
                .build();

        response.setFileList(fileDao.findFilesByNoticeId(id));
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ContentModel.NoticeListDto> findNotices() throws Exception {
        return contentDao.findNoticeList();
    }

    @Transactional(rollbackFor = Exception.class)
    public Page<ContentModel.ContentListDto> findUserContents(CustomUserDetails userDetails, Pageable pageable) throws CustomException {
        return contentDao.findUserContentPages(userDetails.getUsername(), pageable);
    }
}
