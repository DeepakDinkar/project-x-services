package com.qomoi.exception;

public class ExistingUserFoundException extends Exception{
    public ExistingUserFoundException(String message) {
        super(message);
    }
}
