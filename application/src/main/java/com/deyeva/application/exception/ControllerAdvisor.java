package com.deyeva.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RefusalException.class)
    public ResponseEntity<Object> handleCityNotFoundException(
            RefusalException ex, WebRequest request) {

        String body = ex.getMessage();

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
