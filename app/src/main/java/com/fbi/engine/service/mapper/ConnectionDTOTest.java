package com.fbi.engine.service.mapper;

import com.fbi.engine.domain.details.ConnectionDetails;
import com.fbi.engine.service.dto.ConnectionTypeDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Connection entity.
 */
public class ConnectionDTOTest implements Serializable {

	private static final long serialVersionUID = -8858063235031959666L;

	private Long id;

	@NotNull
	@Size(max = 100)
	private String name;

	@NotNull
	@Size(min = 1, max = 255)
	private String connectionUsername;

	@NotNull
	@Size(min = 1, max = 255)
	private String connectionPassword;

	private String linkId;

	private ConnectionTypeDTO connectionType;

	@NotNull
	private ConnectionDetails details;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConnectionUsername() {
		return connectionUsername;
	}

	public void setConnectionUsername(String connectionUsername) {
		this.connectionUsername = connectionUsername;
	}

	public String getConnectionPassword() {
		return connectionPassword;
	}

	public void setConnectionPassword(String connectionPassword) {
		this.connectionPassword = connectionPassword;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public ConnectionDetails getDetails() {
		return details;
	}

	public void setDetails(ConnectionDetails details) {
		this.details = details;
	}

	public ConnectionTypeDTO getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(ConnectionTypeDTO connectionType) {
		this.connectionType = connectionType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ConnectionDTOTest connectionDTOTest = (ConnectionDTOTest) o;
		if (connectionDTOTest.getId() == null || getId() == null) {
			return false;
		}
		return Objects.equals(getId(), connectionDTOTest.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	@Override
	public String toString() {
		return "ConnectionDTO{" + "id=" + getId() + ", name='" + getName() + "'" + ", connectionUsername='"
				+ getConnectionUsername() + "'" + ", connectionPassword='" + getConnectionPassword() + "'"
				+ ", linkId='" + getLinkId() + "'" + "}";
	}
}
