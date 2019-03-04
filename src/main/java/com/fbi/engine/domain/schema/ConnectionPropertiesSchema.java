package com.fbi.engine.domain.schema;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ConnectionPropertiesSchema implements Serializable {

    /*
        Derived instance of ConnectionDetails class that is associated with this connection properties schma
     */
    private String connectionDetailsClass;

    /*
       @type that is used for JSON polymorphic (de)serialization
     */
    private String connectionDetailsType;

    /*
     File system path or url of image that is displayed.
     */
    private String imagePath;

    private List<ConnectionProperty> connectionProperties;


}
