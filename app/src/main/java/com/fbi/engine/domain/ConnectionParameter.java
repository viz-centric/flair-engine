package com.fbi.engine.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Connection.
 */
@Entity
@Table(name = "connection_parameter")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode(of = { "id" }, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
	@SequenceGenerator(name = "sequenceGenerator", sequenceName = "hibernate_sequence", initialValue = 1000, allocationSize = 50)
	private Long id;

	@NotNull
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Size(max = 255)
	@Column(name = "value")
	private String value;

	@Size(max = 255)
	private String linkId;

	public ConnectionParameter name(String name) {
		this.name = name;
		return this;
	}

	public ConnectionParameter value(String value) {
		this.value = value;
		return this;
	}

	public ConnectionParameter linkId(String linkId) {
		this.linkId = linkId;
		return this;
	}

}
