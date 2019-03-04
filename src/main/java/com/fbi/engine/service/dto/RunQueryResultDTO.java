package com.fbi.engine.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RunQueryResultDTO {

    public enum Result {
        OK,
        DATASOURCE_NOT_FOUND,
        INVAILD_QUERY
    }

    private String rawResult;
    private Result resultCode;
}
