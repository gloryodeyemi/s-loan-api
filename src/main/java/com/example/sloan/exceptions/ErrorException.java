package com.example.sloan.exceptions;

import javax.security.sasl.AuthenticationException;

public class ErrorException extends AuthenticationException {
    public ErrorException(final String message) {
        super(message);
    }
}
