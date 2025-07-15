package com.thushima.statusmonitor.shared.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(
            HttpStatus.CONFLICT,
            "RESOURCE_ALREADY_EXISTS",
            String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
            String.format("The %s with %s '%s' already exists in the system", resourceName, fieldName, fieldValue)
        );
    }
}
