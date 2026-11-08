package com.banklite.api.common;

public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException (String message) {
        super(message);
    }
}
