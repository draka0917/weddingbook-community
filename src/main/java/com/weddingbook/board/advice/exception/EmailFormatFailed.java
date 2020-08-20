package com.weddingbook.board.advice.exception;

public class EmailFormatFailed extends RuntimeException {
    public EmailFormatFailed() {
        super();
    }

    public EmailFormatFailed(String message) {
        super(message);
    }

    public EmailFormatFailed(String message, Throwable cause) {
        super(message, cause);
    }
}
