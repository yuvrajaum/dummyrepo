package com.yuvraj.minibank.exception.Account;

public class AccountNotFoundException extends RuntimeException {
     public AccountNotFoundException(String message) {
        super(message);
    }
}
