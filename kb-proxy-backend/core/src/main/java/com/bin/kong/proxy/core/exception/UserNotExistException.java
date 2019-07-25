package com.bin.kong.proxy.core.exception;

public class UserNotExistException extends RuntimeException {
    public UserNotExistException() {
        super("user not found");
    }
}
