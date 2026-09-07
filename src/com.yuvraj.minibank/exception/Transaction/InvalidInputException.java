package com.yuvraj.minibank.exception.Transaction;

public class InvalidInputException extends RuntimeException {
    public  InvalidInputException(String message) {
        super(message);
    }
}
