package com.bol.mancala.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class MyResourcePermissionDeniedException extends RuntimeException {
  public MyResourcePermissionDeniedException() {
    super();
  }

  public MyResourcePermissionDeniedException(String message, Throwable cause) {
    super(message, cause);
  }

  public MyResourcePermissionDeniedException(String message) {
    super(message);
  }

  public MyResourcePermissionDeniedException(Throwable cause) {
    super(cause);
  }
}
