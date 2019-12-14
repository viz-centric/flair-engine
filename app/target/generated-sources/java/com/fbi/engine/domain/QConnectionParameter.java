package com.fbi.engine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QConnectionParameter is a Querydsl query type for ConnectionParameter
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QConnectionParameter extends EntityPathBase<ConnectionParameter> {

    private static final long serialVersionUID = 1815883807L;

    public static final QConnectionParameter connectionParameter = new QConnectionParameter("connectionParameter");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath linkId = createString("linkId");

    public final StringPath name = createString("name");

    public final StringPath value = createString("value");

    public QConnectionParameter(String variable) {
        super(ConnectionParameter.class, forVariable(variable));
    }

    public QConnectionParameter(Path<? extends ConnectionParameter> path) {
        super(path.getType(), path.getMetadata());
    }

    public QConnectionParameter(PathMetadata metadata) {
        super(ConnectionParameter.class, metadata);
    }

}

