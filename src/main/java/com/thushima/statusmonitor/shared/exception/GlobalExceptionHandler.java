package com.thushima.statusmonitor.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBaseException(BaseException ex) {
        log.error("BaseException: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, ex.getStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("BadCredentialsException: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Invalid username or password",
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You don't have permission to access this resource",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("ResourceNotFoundException: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "The requested resource was not found",
                ex.getMessage()
        );
    }
    
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        log.error("ResourceAlreadyExistsException: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                ex.getStatus(),
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getDetails()
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAllUncaughtException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                ex.getMessage()
        );
    }


//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            HttpHeaders headers,
//            HttpStatus status,
//            WebRequest request) {
//
//        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
//                .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
//                .collect(Collectors.joining("; "));
//
//        log.error("Validation error: {}", errorMessage);
//
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.BAD_REQUEST.value())
//                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
//                .code("VALIDATION_ERROR")
//                .message("Validation failed")
//                .details(errorMessage)
//                .path(request.getDescription(false).replace("uri=", ""))
//                .build();
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

    private Mono<ResponseEntity<ErrorResponse>> buildErrorResponse(
            HttpStatus status,
            String errorCode,
            String message,
            String details) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(errorCode)
                .message(message)
                .details(details)
//                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }

    private Mono<ResponseEntity<ErrorResponse>> buildErrorResponse(BaseException ex, HttpStatus status) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .details(ex.getDetails())
//                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }
}
