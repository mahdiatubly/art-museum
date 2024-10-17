package com.tech.altoubli.museum.art.exception;

public class NonAuthorizedActionException extends RuntimeException{
    public NonAuthorizedActionException(String message){ super(message); }
}
