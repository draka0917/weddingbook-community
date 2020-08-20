package com.weddingbook.board.advice.exception;

public class ResourceNotExistException extends RuntimeException {

    public ResourceNotExistException() {
    }

    public ResourceNotExistException(String message) {
        super(message);
    }

    public ResourceNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
