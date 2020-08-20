package com.weddingbook.board.advice.exception;

public class SigninException extends RuntimeException {
    public SigninException() {
        super();
    }

    public SigninException(String message) {
        super(message);
    }

    public SigninException(String message, Throwable cause) {
        super(message, cause);
    }
}
