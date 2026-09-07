package com.yuvraj.minibank.service;

import com.yuvraj.minibank.model.Account;

import java.util.List;

public interface AccountService {
    Account createAccount(String holderName);
  double checkBalance(long accountNumber);
  List<Account> listAccounts();
}