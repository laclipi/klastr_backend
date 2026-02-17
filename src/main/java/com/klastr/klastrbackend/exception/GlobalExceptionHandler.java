package com.klastr.klastrbackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log
            = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 🔥 Business errors (409, 400, etc)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(
            BusinessException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(error);
    }

    // 🔥 System errors (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error at {} -> {}",
                request.getRequestURI(),
                ex.getMessage(),
                ex);

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
