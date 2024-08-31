package com.architecture.archi.db.entity.like;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QContentLikeEntity is a Querydsl query type for ContentLikeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContentLikeEntity extends EntityPathBase<ContentLikeEntity> {

    private static final long serialVersionUID = 1859535552L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentLikeEntity contentLikeEntity = new QContentLikeEntity("contentLikeEntity");

    public final com.architecture.archi.db.entity.content.QContentEntity content;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.architecture.archi.db.entity.user.QUserEntity user;

    public QContentLikeEntity(String variable) {
        this(ContentLikeEntity.class, forVariable(variable), INITS);
    }

    public QContentLikeEntity(Path<? extends ContentLikeEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QContentLikeEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QContentLikeEntity(PathMetadata metadata, PathInits inits) {
        this(ContentLikeEntity.class, metadata, inits);
    }

    public QContentLikeEntity(Class<? extends ContentLikeEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.content = inits.isInitialized("content") ? new com.architecture.archi.db.entity.content.QContentEntity(forProperty("content"), inits.get("content")) : null;
        this.user = inits.isInitialized("user") ? new com.architecture.archi.db.entity.user.QUserEntity(forProperty("user")) : null;
    }

}

