package com.tech.altoubli.museum.art.exception;

public class UserProfileNotFound extends RuntimeException {
    public UserProfileNotFound(String message) {
        super(message);
    }
}
