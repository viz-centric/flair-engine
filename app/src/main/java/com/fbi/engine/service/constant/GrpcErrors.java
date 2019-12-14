package com.fbi.engine.service.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GrpcErrors {

    DUPLICATE_FEATURES("validation.duplicate", "Query contains duplicate features");

    private final String errorCode;
    private final String errorMessage;

}
