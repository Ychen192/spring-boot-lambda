package com.yan.springbootlambda.exception;

import com.yan.springbootlambda.model.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHelper {

    @ExceptionHandler(value = {TransitClientException.class})
    public ResponseEntity<Response> handleTransitClientException(TransitClientException ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(new Response(false, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Response> handleException(Exception ex) {
        return new ResponseEntity<>(new Response(false, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
