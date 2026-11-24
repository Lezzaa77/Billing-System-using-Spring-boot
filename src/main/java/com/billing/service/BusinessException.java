package com.billing.service;

/** A normal, expected problem (for example "not enough stock"). The message is safe to show to the user. */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }
}
