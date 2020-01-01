package com.fbi.engine.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fbi.engine.domain.ConnectionStatus;
import com.fbi.engine.domain.details.ConnectionDetails;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Connection entity.
 */
@Getter
@Setter
public class ConnectionDTO implements Serializable {

	private static final long serialVersionUID = -6829724530915793456L;

	private Long id;

	@NotNull
	@Size(max = 100)
	private String name;

	@NotNull
	@Size(min = 1, max = 255)
	private String connectionUsername;

	@NotNull
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Size(min = 1, max = 255)
	private String connectionPassword;

	private String linkId;

	private ConnectionTypeDTO connectionType;

	@NotNull
	private ConnectionDetails details;

	private ConnectionStatus status;

	@NotNull
	private DriverDTO driver;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ConnectionDTO connectionDTO = (ConnectionDTO) o;
		if (connectionDTO.getId() == null || getId() == null) {
			return false;
		}
		return Objects.equals(getId(), connectionDTO.getId());
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
