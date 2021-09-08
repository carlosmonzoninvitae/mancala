package com.bol.mancala.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MyResourceAlreadyExistsException extends RuntimeException {
    public MyResourceAlreadyExistsException() {
        super();
    }
    public MyResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    public MyResourceAlreadyExistsException(String message) {
        super(message);
    }
    public MyResourceAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
