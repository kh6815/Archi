package com.architecture.archi.db.entity.auth;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTokenPairEntity is a Querydsl query type for TokenPairEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTokenPairEntity extends EntityPathBase<TokenPairEntity> {

    private static final long serialVersionUID = 741615764L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTokenPairEntity tokenPairEntity = new QTokenPairEntity("tokenPairEntity");

    public final StringPath accessToken = createString("accessToken");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final com.architecture.archi.db.entity.user.QUserEntity user;

    public QTokenPairEntity(String variable) {
        this(TokenPairEntity.class, forVariable(variable), INITS);
    }

    public QTokenPairEntity(Path<? extends TokenPairEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTokenPairEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTokenPairEntity(PathMetadata metadata, PathInits inits) {
        this(TokenPairEntity.class, metadata, inits);
    }

    public QTokenPairEntity(Class<? extends TokenPairEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.architecture.archi.db.entity.user.QUserEntity(forProperty("user")) : null;
    }

}

