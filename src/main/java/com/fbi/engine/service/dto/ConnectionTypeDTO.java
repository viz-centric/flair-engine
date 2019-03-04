package com.fbi.engine.service.dto;


import com.fbi.engine.domain.schema.ConnectionPropertiesSchema;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the ConnectionType entity.
 */
public class ConnectionTypeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private String bundleClass;

    private ConnectionPropertiesSchema connectionPropertiesSchema;

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

    public String getBundleClass() {
        return bundleClass;
    }

    public void setBundleClass(String bundleClass) {
        this.bundleClass = bundleClass;
    }

    public ConnectionPropertiesSchema getConnectionPropertiesSchema() {
        return connectionPropertiesSchema;
    }

    public void setConnectionPropertiesSchema(ConnectionPropertiesSchema connectionPropertiesSchema) {
        this.connectionPropertiesSchema = connectionPropertiesSchema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConnectionTypeDTO connectionTypeDTO = (ConnectionTypeDTO) o;
        if(connectionTypeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), connectionTypeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ConnectionTypeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", bundleClass='" + getBundleClass() + "'" +
            "}";
    }
}
