package com.bol.mancala.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidMovementException extends RuntimeException {
  public InvalidMovementException() {
    super();
  }

  public InvalidMovementException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidMovementException(String message) {
    super(message);
  }

  public InvalidMovementException(Throwable cause) {
    super(cause);
  }
}
