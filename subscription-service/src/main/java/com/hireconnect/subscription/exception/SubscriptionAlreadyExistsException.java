package com.hireconnect.subscription.exception;

public class SubscriptionAlreadyExistsException extends RuntimeException {

    public SubscriptionAlreadyExistsException(String message) {
        super(message);
    }
}