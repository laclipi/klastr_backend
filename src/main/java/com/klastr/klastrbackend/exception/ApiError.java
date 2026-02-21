package com.klastr.klastrbackend.exception;

import java.time.LocalDateTime;

public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Object message;
    private String path;

    public ApiError(int status, String error, Object message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Object getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
