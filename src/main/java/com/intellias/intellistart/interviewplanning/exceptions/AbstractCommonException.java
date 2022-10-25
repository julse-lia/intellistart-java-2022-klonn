package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Abstract resource exception.
 */
@Getter
@Setter
public class AbstractCommonException extends RuntimeException {
  protected HttpStatus httpStatus;

  public AbstractCommonException(String message) {
    super(message);
  }
}