package com.yuvraj.minibank.exception.Transaction;

public class InvalidAmountException extends RuntimeException {
     public InvalidAmountException(String message) {
        super(message);
    }
}
