package com.qomoi1.exception;

public class ExistingUserFoundException extends Exception{
    public ExistingUserFoundException(String message) {
        super(message);
    }
}
