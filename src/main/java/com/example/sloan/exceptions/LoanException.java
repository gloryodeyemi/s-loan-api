package com.example.sloan.exceptions;

import javax.security.sasl.AuthenticationException;

public class LoanException extends RuntimeException {
    public LoanException(final String message) {
        super(message);
    }
}
