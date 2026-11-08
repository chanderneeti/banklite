package com.banklite.api.common;

public class BadRequestException extends RuntimeException {

    public BadRequestException (String message) {
        super(message);
    }
}
