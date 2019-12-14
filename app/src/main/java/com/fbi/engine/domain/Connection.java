package com.fbi.engine.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fbi.engine.crypto.converter.ConnectionDetailsCryptoConverter;
import com.fbi.engine.crypto.converter.StringCryptoConverter;
import com.fbi.engine.domain.details.ConnectionDetails;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

/**
 * A Connection.
 */
@Entity
@Table(name = "connection")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Connection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "connection_username", nullable = false)
    private String connectionUsername;

    @NotNull
    @Size(min = 1, max = 255)
    @JsonIgnore
    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "connection_password", nullable = false)
    private String connectionPassword;

    @NotNull
    @Column(name = "link_id", nullable = false, unique = true)
    private String linkId;

    @ManyToOne
    private ConnectionType connectionType;

    @NotNull
    @Convert(converter = ConnectionDetailsCryptoConverter.class)
    @Column(name = "details", nullable = false)
    private ConnectionDetails details;

    @Enumerated(EnumType.STRING)
    private ConnectionStatus status;

    @PrePersist
    public void prePersist() {
        this.linkId = UUID.randomUUID().toString();
    }

    public Connection name(String name) {
        this.name = name;
        return this;
    }

    public Connection connectionUsername(String connectionUsername) {
        this.connectionUsername = connectionUsername;
        return this;
    }

    public Connection connectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
        return this;
    }

    public Connection connectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
        return this;
    }

    public Connection linkId(String linkId) {
        this.linkId = linkId;
        return this;
    }


}
