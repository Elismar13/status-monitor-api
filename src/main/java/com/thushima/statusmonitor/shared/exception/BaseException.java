package com.thushima.statusmonitor.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;
    private final String details;

    protected BaseException(HttpStatus status, String errorCode, String message, String details) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.details = details;
    }

    protected BaseException(HttpStatus status, String errorCode, String message) {
        this(status, errorCode, message, null);
    }
}
