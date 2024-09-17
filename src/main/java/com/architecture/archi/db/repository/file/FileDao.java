package com.architecture.archi.db.repository.file;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.content.file.model.FileModel;
import com.architecture.archi.db.entity.category.QCategoryEntity;
import com.architecture.archi.db.entity.content.*;
import com.architecture.archi.db.entity.file.FileEntity;
import com.architecture.archi.db.entity.file.QFileEntity;
import com.architecture.archi.db.entity.like.QContentLikeEntity;
import com.architecture.archi.db.entity.notice.NoticeEntity;
import com.architecture.archi.db.entity.notice.NoticeFileEntity;
import com.architecture.archi.db.entity.notice.QNoticeEntity;
import com.architecture.archi.db.entity.notice.QNoticeFileEntity;
import com.architecture.archi.db.entity.user.QUserEntity;
import com.architecture.archi.db.entity.user.QUserFileEntity;
import com.architecture.archi.db.entity.user.UserFileEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
@Repository
public class FileDao {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    private final QUserEntity qUserEntity = QUserEntity.userEntity;
    private final QCategoryEntity qCategoryEntity = QCategoryEntity.categoryEntity;
    private final QContentEntity qContentEntity = QContentEntity.contentEntity;

    private final QContentFileEntity qContentFileEntity = QContentFileEntity.contentFileEntity;

    private final QContentLikeEntity qContentLikeEntity = QContentLikeEntity.contentLikeEntity;

    private final QBestContentEntity qBestContentEntity = QBestContentEntity.bestContentEntity;

    private final QFileEntity qFileEntity = QFileEntity.fileEntity;

    private final QNoticeEntity qNoticeEntity = QNoticeEntity.noticeEntity;

    private final QNoticeFileEntity qNoticeFileEntity = QNoticeFileEntity.noticeFileEntity;

    private final QUserFileEntity qUserFileEntity = QUserFileEntity.userFileEntity;


    public List<FileModel.FileRes> findFilesByContentId(Long contentId) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(FileModel.FileRes.class,
                                qContentFileEntity.file.id,
                                qContentFileEntity.file.url
                        )

                )
                .from(qContentFileEntity)
                .where(
                        qContentFileEntity.delYn.eq(BooleanFlag.N)
                                .and(qContentFileEntity.content.id.eq(contentId))
                )
                .orderBy(qContentFileEntity.file.name.asc())
                .fetch();
    }

    public List<FileModel.FileRes> findFilesByNoticeId(Long noticeId) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(FileModel.FileRes.class,
                                qNoticeFileEntity.file.id,
                                qNoticeFileEntity.file.url
                        )

                )
                .from(qNoticeFileEntity)
                .where(
                        qNoticeFileEntity.delYn.eq(BooleanFlag.N)
                                .and(qNoticeFileEntity.notice.id.eq(noticeId))
                )
                .orderBy(qNoticeFileEntity.file.name.asc())
                .fetch();
    }

    public Map<String, String> findFileUrlByUserIds(List<String> userIds){
        List<UserFileEntity> userFileEntityList = jpaQueryFactory
                .selectFrom(qUserFileEntity)
                .leftJoin(qUserFileEntity.user, qUserEntity).fetchJoin()
                .leftJoin(qUserFileEntity.file, qFileEntity).fetchJoin()
                .where(qUserFileEntity.user.id.in(userIds)
                        .and(qUserFileEntity.delYn.eq(BooleanFlag.N))
                )
                .fetch();

        // 각 contentId에 대해 하나의 ContentFileEntity만 반환
        return userFileEntityList.stream()
                .collect(Collectors.toMap(
                        userFileEntity -> userFileEntity.getUser().getId(),
                        userFileEntity -> userFileEntity.getFile().getUrl()
                ));
    }

    public List<FileEntity> findFileListByFileIds(List<Long> fileIds){
        return jpaQueryFactory
                .selectFrom(qFileEntity)
                .where(qFileEntity.id.in(fileIds))
                .fetch();
    }
}
