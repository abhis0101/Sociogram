package com.sociogram.main_service.exception;

public class ErrorResponse {
    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}