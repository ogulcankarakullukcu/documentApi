package com.example.documentapi.exception;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(){
        super("File Not Found");
    }
}
