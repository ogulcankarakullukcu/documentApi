package com.example.documentapi.exception;

public class DocumentServiceException extends RuntimeException {
    public DocumentServiceException(String message){
        super(message);
    }
}
