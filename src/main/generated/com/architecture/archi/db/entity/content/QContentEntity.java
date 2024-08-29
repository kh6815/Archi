package com.architecture.archi.db.entity.content;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QContentEntity is a Querydsl query type for ContentEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContentEntity extends EntityPathBase<ContentEntity> {

    private static final long serialVersionUID = 88246947L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentEntity contentEntity = new QContentEntity("contentEntity");

    public final com.architecture.archi.db.entity.category.QCategoryEntity category;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<com.architecture.archi.common.enumobj.BooleanFlag> delYn = createEnum("delYn", com.architecture.archi.common.enumobj.BooleanFlag.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> like = createNumber("like", Integer.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final com.architecture.archi.db.entity.user.QUserEntity user;

    public QContentEntity(String variable) {
        this(ContentEntity.class, forVariable(variable), INITS);
    }

    public QContentEntity(Path<? extends ContentEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QContentEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QContentEntity(PathMetadata metadata, PathInits inits) {
        this(ContentEntity.class, metadata, inits);
    }

    public QContentEntity(Class<? extends ContentEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.architecture.archi.db.entity.category.QCategoryEntity(forProperty("category"), inits.get("category")) : null;
        this.user = inits.isInitialized("user") ? new com.architecture.archi.db.entity.user.QUserEntity(forProperty("user")) : null;
    }

}

