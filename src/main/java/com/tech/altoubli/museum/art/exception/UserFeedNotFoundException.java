package com.tech.altoubli.museum.art.exception;

public class UserFeedNotFoundException extends RuntimeException {
    public UserFeedNotFoundException(String message){
        super(message);
    }
}
