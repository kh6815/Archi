package com.architecture.archi.content.content.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.db.entity.content.ContentEntity;
import com.architecture.archi.db.repository.content.ContentDao;
import com.architecture.archi.db.repository.content.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ContentReadService {

    private final ContentRepository contentRepository;
    private final ContentDao contentDao;

    @Transactional(rollbackFor = Exception.class)
    public ContentModel.ContentDto findContent(Long id, CustomUserDetails userDetails) throws CustomException {
        // 컨텐츠와 연관된 user, category를 모두 가져오기
        ContentEntity contentEntity = contentDao.findContent(id);

        return ContentModel.ContentDto.builder()
                .id(contentEntity.getId())
                .title(contentEntity.getTitle())
                .content(contentEntity.getContent())
                .categoryName(contentEntity.getCategory().getCategoryName())
                .isAvailableUpdate(contentEntity.getUser().getId().equals(userDetails.getUsername()))
                .updatedAt(contentEntity.getUpdatedAt())
                .like((long) contentEntity.getContentLikes().size())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public Page<ContentModel.ContentListDto> findContents(Long categoryId, Pageable pageable) throws Exception {
        return contentDao.findContentPages(categoryId, pageable);
    }
}
