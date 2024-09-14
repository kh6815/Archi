package com.architecture.archi.content.content.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
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

    @Transactional(rollbackFor = Exception.class)
    public ContentModel.ContentDto findContent(Long id, CustomUserDetails userDetails) throws CustomException {
        // 컨텐츠와 연관된 user, category를 모두 가져오기
        ContentEntity contentEntity = contentDao.findContent(id);

        String contentAuthorImgUrl = null;
        Optional<UserFileEntity> userFileEntityOptional = userDao.findUserFileWithFileByUserId(contentEntity.getUser().getId());

        if(userFileEntityOptional.isPresent()){
            contentAuthorImgUrl = userFileEntityOptional.get().getFile().getUrl();
        }

        ContentModel.ContentDto response = ContentModel.ContentDto.builder()
                .id(contentEntity.getId())
                .title(contentEntity.getTitle())
                .content(contentEntity.getContent())
                .categoryName(contentEntity.getCategory().getCategoryName())
                .isAvailableUpdate(contentEntity.getUser().getId().equals(userDetails.getUsername()))
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
    public Page<ContentModel.ContentListDto> findContents(Long categoryId, Pageable pageable) throws Exception {
        return contentDao.findContentPages(categoryId, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public ContentModel.NoticeDto findNotice(Long id, CustomUserDetails userDetails) throws CustomException {
        NoticeEntity noticeEntity = contentDao.findNotice(id);

        return ContentModel.NoticeDto.builder()
                .id(noticeEntity.getId())
                .title(noticeEntity.getTitle())
                .content(noticeEntity.getContent())
                .isAvailableUpdate(noticeEntity.getUser().getId().equals(userDetails.getUsername()))
                .updatedAt(noticeEntity.getUpdatedAt())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ContentModel.NoticeListDto> findNotices() throws Exception {
        return contentDao.findNoticeList();
    }
}
