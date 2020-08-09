package com.fbi.engine.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
public class QueryAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actor;

    private String query;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private QueryAuditLogMetadata meta;

    private String connectionLinkId;

    private Instant createdDate;


}
