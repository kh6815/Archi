package com.architecture.archi.db.entity.content;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBestContentEntity is a Querydsl query type for BestContentEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBestContentEntity extends EntityPathBase<BestContentEntity> {

    private static final long serialVersionUID = -589657185L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBestContentEntity bestContentEntity = new QBestContentEntity("bestContentEntity");

    public final QContentEntity content;

    public final NumberPath<Integer> contentRank = createNumber("contentRank", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBestContentEntity(String variable) {
        this(BestContentEntity.class, forVariable(variable), INITS);
    }

    public QBestContentEntity(Path<? extends BestContentEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBestContentEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBestContentEntity(PathMetadata metadata, PathInits inits) {
        this(BestContentEntity.class, metadata, inits);
    }

    public QBestContentEntity(Class<? extends BestContentEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.content = inits.isInitialized("content") ? new QContentEntity(forProperty("content"), inits.get("content")) : null;
    }

}

