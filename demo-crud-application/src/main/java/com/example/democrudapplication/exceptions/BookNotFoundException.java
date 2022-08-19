package com.example.democrudapplication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * При выбросе данного exception, будет вызван автоматически
 * обработчик данное ошибки, который сформирует response клиенту,
 * с кодом ошибки и причиной.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(String message) {
    super(message);
  }
}
