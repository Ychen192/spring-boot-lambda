package com.yan.springbootlambda.exception;

public class TransitClientException extends Exception {
    public TransitClientException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public TransitClientException(){
        super();
    }
}
