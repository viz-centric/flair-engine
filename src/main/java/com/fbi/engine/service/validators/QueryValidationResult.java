package com.fbi.engine.service.validators;

import com.fbi.engine.service.constant.GrpcErrors;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
public class QueryValidationResult {
    private GrpcErrors errors;
    private Set<String> featureNames;

    public boolean isSuccess() {
        return !isError();
    }

    public boolean isError() {
        return errors != null;
    }
}
