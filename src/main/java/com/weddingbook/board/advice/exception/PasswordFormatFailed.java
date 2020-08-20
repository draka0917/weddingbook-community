package com.weddingbook.board.advice.exception;

public class PasswordFormatFailed extends RuntimeException {
    public PasswordFormatFailed() {
        super();
    }

    public PasswordFormatFailed(String message) {
        super(message);
    }

    public PasswordFormatFailed(String message, Throwable cause) {
        super(message, cause);
    }
}
