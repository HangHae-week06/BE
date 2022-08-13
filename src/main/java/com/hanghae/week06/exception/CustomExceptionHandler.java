package com.hanghae.week06.exception;

import com.hanghae.week06.controller.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException exception) {


        String errorMessage = exception.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return new ResponseEntity( ResponseDto.fail("BAD_REQUEST",errorMessage), HttpStatus.BAD_REQUEST );
    }

}