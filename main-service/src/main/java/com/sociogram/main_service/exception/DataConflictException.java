package com.sociogram.main_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
