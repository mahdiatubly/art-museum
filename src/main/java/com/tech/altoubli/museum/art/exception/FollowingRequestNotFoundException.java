package com.tech.altoubli.museum.art.exception;

public class FollowingRequestNotFoundException extends RuntimeException {
    public FollowingRequestNotFoundException(String message){
        super(message);
    }
}
