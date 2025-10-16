package com.taurasg.altpro.api.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}