package com.example.sloan.exceptions;

import javax.security.sasl.AuthenticationException;

public class TransactionException extends RuntimeException {
    public TransactionException(final String message) {
        super(message);
    }
}
