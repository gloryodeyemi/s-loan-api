package com.example.sloan.exceptions;

import javax.security.sasl.AuthenticationException;

public class AccountException extends RuntimeException {
    public AccountException(final String message) {
        super(message);
    }
}
