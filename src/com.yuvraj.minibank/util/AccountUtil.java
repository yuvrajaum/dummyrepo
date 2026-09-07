package com.yuvraj.minibank.util;

import com.yuvraj.minibank.repository.BankRepository;

public class AccountUtil {
    public static boolean isAccountExists(long accountNumber, BankRepository repo) {
        return repo.isAccountExists(accountNumber);
    }
}
