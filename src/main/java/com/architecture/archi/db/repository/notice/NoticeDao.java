package com.architecture.archi.db.repository.notice;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.db.entity.category.QCategoryEntity;
import com.architecture.archi.db.entity.content.*;
import com.architecture.archi.db.entity.file.QFileEntity;
import com.architecture.archi.db.entity.like.QContentLikeEntity;
import com.architecture.archi.db.entity.notice.NoticeEntity;
import com.architecture.archi.db.entity.notice.NoticeFileEntity;
import com.architecture.archi.db.entity.notice.QNoticeEntity;
import com.architecture.archi.db.entity.notice.QNoticeFileEntity;
import com.architecture.archi.db.entity.user.QUserEntity;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class NoticeDao {

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

    public NoticeEntity findNotice(Long id) throws CustomException {
        return Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qNoticeEntity)
                                .leftJoin(qNoticeEntity.user, qUserEntity).fetchJoin()
                                .where(qNoticeEntity.id.eq(id)
                                        .and(qNoticeEntity.delYn.eq(BooleanFlag.N)))
                                .fetchOne()

                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, String.format("Notice [%s] is null", id)));
    }

    public List<NoticeFileEntity> findNoticeFileByNoticeId(Long noticeId) throws CustomException {
        return
                jpaQueryFactory
                        .selectFrom(qNoticeFileEntity)
                        .where(qNoticeFileEntity.notice.id.eq(noticeId)
                                .and(qNoticeFileEntity.delYn.eq(BooleanFlag.N)))
                        .fetch();
    }

    public List<NoticeFileEntity> findNoticeFileByNoticeIds(List<Long> noticeIds) throws CustomException{
        return
                jpaQueryFactory
                        .selectFrom(qNoticeFileEntity)
                        .where(qNoticeFileEntity.notice.id.in(noticeIds))
                        .fetch();
    }

    public List<NoticeFileEntity> findNoticeFileByFileIds(List<Long> fileIds) throws CustomException{
        return
                jpaQueryFactory
                        .selectFrom(qNoticeFileEntity)
                        .where(qNoticeFileEntity.file.id.in(fileIds))
                        .fetch();
    }

    public void updateDelYnContentNoticeListByNoticeIds(List<Long> noticeIds) {
        jpaQueryFactory
                .update(qNoticeFileEntity)
                .set(qNoticeFileEntity.delYn, BooleanFlag.Y)
                .where(qNoticeFileEntity.id.in(noticeIds))
                .execute();
    }
}
