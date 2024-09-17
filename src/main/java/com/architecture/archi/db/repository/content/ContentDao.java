package com.architecture.archi.db.repository.content;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.content.content.model.ContentModel;
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
public class ContentDao {

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


    public Page<ContentModel.ContentListDto> findContentPages(Long categoryId, Pageable pageable) throws Exception {

        List<ContentModel.ContentListDto> contentListDtoList = jpaQueryFactory
                .select(
                        Projections.constructor(ContentModel.ContentListDto.class,
                        qContentEntity.id,
                                qContentEntity.category.categoryName,
                                qContentEntity.title,
                                qContentEntity.content,
                                qContentEntity.updatedAt,
                                // 서브쿼리를 사용하여 해당 content의 좋아요 수를 계산
                                JPAExpressions
                                        .select(qContentLikeEntity.count())
                                        .from(qContentLikeEntity)
                                        .where(qContentLikeEntity.content.id.eq(qContentEntity.id))
                        )

                )
                .from(qContentEntity)
                .where(
                        qContentEntity.delYn.eq(BooleanFlag.N)
                                .and(dynamicContentCategoryBuilder(categoryId))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qContentEntity.createdAt.desc())
//                .distinct()
                .fetch();

        List<Long> contentIds = contentListDtoList.stream()
                .map(ContentModel.ContentListDto::getId)
                .toList();

        Map<Long, ContentFileEntity> contentFileEntityMap = findSingleContentFileByContentIds(contentIds);

        for (ContentModel.ContentListDto contentListDto : contentListDtoList) {
            if(contentFileEntityMap.containsKey(contentListDto.getId())){
                FileEntity file = contentFileEntityMap.get(contentListDto.getId()).getFile();
                contentListDto.setImgUrl(file.getUrl());
            }
        }

        long total = jpaQueryFactory
                .selectFrom(qContentEntity)
                .where(
                        qContentEntity.delYn.eq(BooleanFlag.N)
                                .and(dynamicContentCategoryBuilder(categoryId))
                )
                .stream().count();
        return new PageImpl<>(contentListDtoList, pageable, total);
    }

    public ContentEntity findContent(Long id) throws CustomException {
        return Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qContentEntity)
                                .leftJoin(qContentEntity.category, qCategoryEntity).fetchJoin()
                                .leftJoin(qContentEntity.user, qUserEntity).fetchJoin()
                                .leftJoin(qContentEntity.contentLikes, qContentLikeEntity).fetchJoin()
                                .where(qContentEntity.id.eq(id)
                                        .and(qContentEntity.delYn.eq(BooleanFlag.N)))
                                .fetchOne()

                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, String.format("Content [%s] is null", id)));
    }

    public List<String> findLikeUserIdListByContentLikeIds(List<Long> contentLikeListIds) throws CustomException {
        return Optional.ofNullable(
                        jpaQueryFactory
                                .select(qContentLikeEntity.user.id)
                                .from(qContentLikeEntity)
                                .where(qContentLikeEntity.id.in(contentLikeListIds))
                                .fetch()

                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 like"));
    }

    public Map<Long, List<ContentFileEntity>> findContentFileListByContentIds(List<Long> contentIds) throws CustomException {
        List<ContentFileEntity> contentFileEntities = Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qContentFileEntity)
                                .leftJoin(qContentFileEntity.file, qFileEntity).fetchJoin()
                                .where(qContentFileEntity.content.id.in(contentIds))
                                .fetch()
                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, String.format("BestContent not found")));

        // contentId를 기준으로 List<ContentFileEntity>를 그룹화
        return contentFileEntities.stream()
                .collect(Collectors.groupingBy(contentFileEntity -> contentFileEntity.getContent().getId()));
    }

    public Map<Long, ContentFileEntity> findSingleContentFileByContentIds(List<Long> contentIds) throws CustomException {
        QContentFileEntity qContentFileEntity2 = new QContentFileEntity("qContentFileEntity2");

//        List<ContentFileEntity> contentFileEntities = Optional.ofNullable(
//                        jpaQueryFactory
//                                .selectFrom(qContentFileEntity)
//                                .leftJoin(qContentFileEntity.file, qFileEntity).fetchJoin()
//                                .where(qContentFileEntity.id.in(
//                                        JPAExpressions.select(qContentFileEntity2.id)
//                                        .from(qContentFileEntity2)
//                                        .where(qContentFileEntity2.content.id.in(contentIds)
//                                                .and(qContentFileEntity2.delYn.eq(BooleanFlag.N)))
//                                        .groupBy(qContentFileEntity2.content.id))
//                                )
//                                .fetch()
//                )
//                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "not found"));

        List<ContentFileEntity> contentFileEntities = Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qContentFileEntity)
                                .leftJoin(qContentFileEntity.file, qFileEntity).fetchJoin()
                                .where(qContentFileEntity.id.in(
                                        JPAExpressions.select(qContentFileEntity2.id)
                                                .from(qContentFileEntity2)
                                                .where(qContentFileEntity2.content.id.in(contentIds)
                                                        .and(qContentFileEntity2.delYn.eq(BooleanFlag.N)))
                                                .groupBy(qContentFileEntity2.content.id))
                                )
                                .where(qFileEntity.originName.contains("image0"))
                                .fetch()
                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "not found"));

//        // 각 contentId에 대해 하나의 ContentFileEntity만 반환
        return contentFileEntities.stream()
                .collect(Collectors.toMap(
                        contentFileEntity -> contentFileEntity.getContent().getId(),
                        contentFileEntity -> contentFileEntity
                ));
//        List<ContentFileEntity> contentFileEntities = Optional.ofNullable(
//                        jpaQueryFactory
//                                .selectFrom(qContentFileEntity)
//                                .leftJoin(qContentFileEntity.file, qFileEntity).fetchJoin()
//                                .where(qContentFileEntity.content.id.in(contentIds))
//                                .limit(1)
//                                .fetch()
//                )
//                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "not found"));
//
//        // 각 contentId에 대해 하나의 ContentFileEntity만 반환
//        return contentFileEntities.stream()
//                .collect(Collectors.toMap(
//                        contentFileEntity -> contentFileEntity.getContent().getId(),
//                        contentFileEntity -> contentFileEntity
//                ));

        // MIN -> 이걸통해 첫번째 파일만 가져옴.
//        List<ContentFileEntity> contentFileEntities = entityManager.createQuery(
//                        "SELECT cf FROM ContentFileEntity cf " +
//                                "WHERE cf.id IN (" +
//                                "SELECT MIN(cf2.id) FROM ContentFileEntity cf2 " +
//                                "WHERE cf2.content.id IN :contentIds " +
//                                "GROUP BY cf2.content.id)", ContentFileEntity.class)
//                .setParameter("contentIds", contentIds)
//                .getResultList();
//
//        if (contentFileEntities.isEmpty()) {
//            throw new CustomException(ExceptionCode.NOT_EXIST, "not found");
//        }
//
//        return contentFileEntities.stream()
//                .collect(Collectors.toMap(
//                        contentFileEntity -> contentFileEntity.getContent().getId(),
//                        contentFileEntity -> contentFileEntity
//                ));
    }

    public Map<Long, NoticeFileEntity> findSingleNoticeFileByNoticeIds(List<Long> noticeIds) throws CustomException {
        QNoticeFileEntity qNoticeFileEntity2 = new QNoticeFileEntity("qNoticeFileEntity2");

        List<NoticeFileEntity> noticeFileEntityList = Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qNoticeFileEntity)
                                .leftJoin(qNoticeFileEntity.file, qFileEntity).fetchJoin()
                                .where(qNoticeFileEntity.id.in(
                                        JPAExpressions.select(qNoticeFileEntity2.id.min())
                                                .from(qNoticeFileEntity2)
                                                .where(qNoticeFileEntity2.notice.id.in(noticeIds))
                                                .groupBy(qNoticeFileEntity2.notice.id))
                                )
                                .fetch()
                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "not found"));

        return noticeFileEntityList.stream()
                .collect(Collectors.toMap(
                        noticeFileEntity -> noticeFileEntity.getNotice().getId(),
                        noticeFileEntity -> noticeFileEntity
                ));
    }

    public List<BestContentEntity> findPopularContent() throws CustomException {
        return Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qBestContentEntity)
                                .leftJoin(qBestContentEntity.content, qContentEntity).fetchJoin()
                                .leftJoin(qContentEntity.category, qCategoryEntity).fetchJoin()
                                .leftJoin(qContentEntity.contentLikes, qContentLikeEntity).fetchJoin()
//                                .leftJoin(qContentEntity.contentFiles, qContentFileEntity).fetchJoin()
//                                .leftJoin(qContentFileEntity.file, qFileEntity).fetchJoin()
                                .where(qBestContentEntity.content.delYn.eq(BooleanFlag.N))
                                .orderBy(qBestContentEntity.contentRank.asc())
                                .fetch()
                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, String.format("BestContent not")));
    }

    public ContentEntity findSimpleContent(Long id) throws CustomException {
        return Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qContentEntity)
                                .where(qContentEntity.id.eq(id)
                                        .and(qContentEntity.delYn.eq(BooleanFlag.N)))
                                .fetchOne()

                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, String.format("Content [%s] is null", id)));
    }

    public ContentEntity findContentWithUser(Long id) throws CustomException {
        return Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qContentEntity)
                                .leftJoin(qContentEntity.user, qUserEntity).fetchJoin()
                                .where(qContentEntity.id.eq(id)
                                        .and(qContentEntity.delYn.eq(BooleanFlag.N)))
                                .fetchOne()

                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, String.format("Content [%s] is null", id)));
    }

    public List<ContentFileEntity> findContentFileByContentId(Long contentId) throws CustomException {
        return
                Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qContentFileEntity)
                                .where(qContentFileEntity.content.id.eq(contentId)
                                    .and(qContentFileEntity.delYn.eq(BooleanFlag.N)))
                                .fetch()
                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "해당하는 데이터가 없습니다."));
    }

    public List<ContentFileEntity> findContentFileByContentIds(List<Long> contentIds) throws CustomException{
        return
                Optional.ofNullable(
                                jpaQueryFactory
                                        .selectFrom(qContentFileEntity)
                                        .where(qContentFileEntity.content.id.in(contentIds))
                                        .fetch()
                        )
                        .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "해당하는 데이터가 없습니다."));
    }

    private BooleanBuilder dynamicContentCategoryBuilder(Long categoryId) throws Exception {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (categoryId != 0) {
            return booleanBuilder.and(qContentEntity.category.id.eq(categoryId));
        }

        return booleanBuilder;
    }

    public List<ContentModel.NoticeListDto> findNoticeList() throws Exception {

        List<ContentModel.NoticeListDto> noticeListDtoList = jpaQueryFactory
                .select(
                        Projections.constructor(ContentModel.NoticeListDto.class,
                                qNoticeEntity.id,
                                qNoticeEntity.title,
                                qNoticeEntity.content,
                                qNoticeEntity.updatedAt
                        )

                )
                .from(qNoticeEntity)
                .where(
                        qNoticeEntity.delYn.eq(BooleanFlag.N)
                )
                .orderBy(qNoticeEntity.createdAt.desc())
                .fetch();

        List<Long> noticeIds = noticeListDtoList.stream()
                .map(ContentModel.NoticeListDto::getId)
                .toList();

        Map<Long, NoticeFileEntity> noticeFileEntityMap = findSingleNoticeFileByNoticeIds(noticeIds);

        for (ContentModel.NoticeListDto noticeListDto : noticeListDtoList) {
            if(noticeFileEntityMap.containsKey(noticeListDto.getId())){
                FileEntity file = noticeFileEntityMap.get(noticeListDto.getId()).getFile();
                noticeListDto.setImgUrl(file.getUrl());
            }
        }

        return noticeListDtoList;
    }

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

    public List<ContentFileEntity> findContentFileByFileIds(List<Long> fileIds) throws CustomException{
        return
                Optional.ofNullable(
                                jpaQueryFactory
                                        .selectFrom(qContentFileEntity)
                                        .where(qContentFileEntity.file.id.in(fileIds))
                                        .fetch()
                        )
                        .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "해당하는 데이터가 없습니다."));
    }

    public void updateDelYnContentFileListByContentIds(List<Long> contentIds) {
        jpaQueryFactory
                .update(qContentFileEntity)
                .set(qContentFileEntity.delYn, BooleanFlag.Y)
                .where(qContentFileEntity.id.in(contentIds))
                .execute();
    }
}
