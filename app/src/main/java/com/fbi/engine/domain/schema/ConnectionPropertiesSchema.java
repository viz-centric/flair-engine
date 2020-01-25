package com.fbi.engine.domain.schema;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ConnectionPropertiesSchema implements Serializable {

	private static final long serialVersionUID = -7307650425760114794L;

	/*
	 * Derived instance of ConnectionDetails class that is associated with this
	 * connection properties schma
	 */
	private String connectionDetailsClass;

	/*
	 * @type that is used for JSON polymorphic (de)serialization
	 */
	private String connectionDetailsType;

	/*
	 * File system path or url of image that is displayed.
	 */
	private String imagePath;

	private List<ConnectionProperty> connectionProperties;

}
