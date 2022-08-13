package com.hanghae.week06.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RestApiException {
    private boolean success;
    private HttpStatus httpStatus;
    private String errorMessage;
}