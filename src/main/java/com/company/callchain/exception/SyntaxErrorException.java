package com.company.callchain.exception;

public class SyntaxErrorException extends RuntimeException {

    public SyntaxErrorException() {
    }

    public SyntaxErrorException(String message) {
        super(message);
    }
}
