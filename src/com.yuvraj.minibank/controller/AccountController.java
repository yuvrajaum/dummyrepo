package com.yuvraj.minibank.controller;
import  com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.service.AccountService;

import java.util.List;

public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService)
    {
        this.accountService = accountService;
    }
    public Account createAccount(String holderName)
    {
        return accountService.createAccount(holderName);
    }
    public double checkBalance(long accountNumber)

    {
        return accountService.checkBalance(accountNumber);
    }
    public List<Account> listAccounts()
    {
        return accountService.listAccounts();
    }

}
