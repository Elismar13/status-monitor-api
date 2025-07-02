package com.thushima.statusmonitor.shared.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    public AuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_ERROR", message);
    }

    public AuthenticationException(String message, String details) {
        super(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_ERROR", message, details);
    }
}
