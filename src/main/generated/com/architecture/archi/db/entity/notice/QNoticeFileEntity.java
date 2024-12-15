package com.architecture.archi.db.entity.notice;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNoticeFileEntity is a Querydsl query type for NoticeFileEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNoticeFileEntity extends EntityPathBase<NoticeFileEntity> {

    private static final long serialVersionUID = -1301362519L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNoticeFileEntity noticeFileEntity = new QNoticeFileEntity("noticeFileEntity");

    public final EnumPath<com.architecture.archi.common.enumobj.BooleanFlag> delYn = createEnum("delYn", com.architecture.archi.common.enumobj.BooleanFlag.class);

    public final com.architecture.archi.db.entity.file.QFileEntity file;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QNoticeEntity notice;

    public QNoticeFileEntity(String variable) {
        this(NoticeFileEntity.class, forVariable(variable), INITS);
    }

    public QNoticeFileEntity(Path<? extends NoticeFileEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNoticeFileEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNoticeFileEntity(PathMetadata metadata, PathInits inits) {
        this(NoticeFileEntity.class, metadata, inits);
    }

    public QNoticeFileEntity(Class<? extends NoticeFileEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.file = inits.isInitialized("file") ? new com.architecture.archi.db.entity.file.QFileEntity(forProperty("file")) : null;
        this.notice = inits.isInitialized("notice") ? new QNoticeEntity(forProperty("notice"), inits.get("notice")) : null;
    }

}

