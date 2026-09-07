package com.yuvraj.minibank.exception.Account;

public class InvalidAccountName extends RuntimeException {
    public  InvalidAccountName(String message) {
        super(message);
    }
}
