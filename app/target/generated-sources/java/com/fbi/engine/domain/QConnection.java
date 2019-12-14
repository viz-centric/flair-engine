package com.fbi.engine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConnection is a Querydsl query type for Connection
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QConnection extends EntityPathBase<Connection> {

    private static final long serialVersionUID = -1025768790L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConnection connection = new QConnection("connection");

    public final StringPath connectionPassword = createString("connectionPassword");

    public final QConnectionType connectionType;

    public final StringPath connectionUsername = createString("connectionUsername");

    public final SimplePath<com.fbi.engine.domain.details.ConnectionDetails> details = createSimple("details", com.fbi.engine.domain.details.ConnectionDetails.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath linkId = createString("linkId");

    public final StringPath name = createString("name");

    public final EnumPath<ConnectionStatus> status = createEnum("status", ConnectionStatus.class);

    public QConnection(String variable) {
        this(Connection.class, forVariable(variable), INITS);
    }

    public QConnection(Path<? extends Connection> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConnection(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConnection(PathMetadata metadata, PathInits inits) {
        this(Connection.class, metadata, inits);
    }

    public QConnection(Class<? extends Connection> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.connectionType = inits.isInitialized("connectionType") ? new QConnectionType(forProperty("connectionType")) : null;
    }

}

