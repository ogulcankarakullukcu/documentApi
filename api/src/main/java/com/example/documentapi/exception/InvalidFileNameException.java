package com.example.documentapi.exception;

public class InvalidFileNameException extends RuntimeException {
    public InvalidFileNameException(String message){
        super(message);
    }
}
