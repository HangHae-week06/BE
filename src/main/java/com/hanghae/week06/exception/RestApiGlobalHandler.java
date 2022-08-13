package com.hanghae.week06.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class RestApiGlobalHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class,
            NullPointerException.class,
            NoSuchElementException.class,
            IllegalStateException.class})

    public ResponseEntity<Object> handleApiRequestException(Exception ex) {
        RestApiException restApiException = new RestApiException();
        restApiException.setErrorMessage(ex.getMessage());

        if(ex.getClass() == IllegalArgumentException.class) {
            restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
        }
        else if(ex.getClass() == NullPointerException.class) {
            restApiException.setHttpStatus(HttpStatus.NOT_FOUND);
        }
        else if(ex.getClass() == NoSuchElementException.class) {
            restApiException.setHttpStatus(HttpStatus.CONFLICT);
        }
        else if(ex.getClass() == IllegalStateException.class) {
            restApiException.setHttpStatus(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }
}
