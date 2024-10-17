package com.tech.altoubli.museum.art.exception;

public class UsernameNotUniqueException extends RuntimeException {
    public UsernameNotUniqueException(String message){
        super(message);
    }
}
