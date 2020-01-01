package com.fbi.engine.service.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverDTO {

	@NotNull
	private byte[] jar;
	@Pattern(regexp = "^[a-zA-Z0-9-]+$")
	private String artifactId;
	@Pattern(regexp = "^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_]$")
	private String groupId;
	private String version;

}
