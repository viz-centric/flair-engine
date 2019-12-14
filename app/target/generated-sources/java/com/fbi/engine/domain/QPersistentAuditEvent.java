package com.fbi.engine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPersistentAuditEvent is a Querydsl query type for PersistentAuditEvent
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPersistentAuditEvent extends EntityPathBase<PersistentAuditEvent> {

    private static final long serialVersionUID = -1557420478L;

    public static final QPersistentAuditEvent persistentAuditEvent = new QPersistentAuditEvent("persistentAuditEvent");

    public final DateTimePath<java.time.Instant> auditEventDate = createDateTime("auditEventDate", java.time.Instant.class);

    public final StringPath auditEventType = createString("auditEventType");

    public final MapPath<String, String, StringPath> data = this.<String, String, StringPath>createMap("data", String.class, String.class, StringPath.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath principal = createString("principal");

    public QPersistentAuditEvent(String variable) {
        super(PersistentAuditEvent.class, forVariable(variable));
    }

    public QPersistentAuditEvent(Path<? extends PersistentAuditEvent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPersistentAuditEvent(PathMetadata metadata) {
        super(PersistentAuditEvent.class, metadata);
    }

}

