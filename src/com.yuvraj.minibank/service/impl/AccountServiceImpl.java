package com.yuvraj.minibank.service.impl;

import  com.yuvraj.minibank.exception.Account.AccountNotFoundException;
import  com.yuvraj.minibank.exception.Account.InvalidAccountName;
import  com.yuvraj.minibank.model.Account;
import  com.yuvraj.minibank.repository.BankRepository;
import  com.yuvraj.minibank.service.AccountService;
import com.yuvraj.minibank.util.AccountUtil;

import java.security.SecureRandom;
import java.util.List;

public class AccountServiceImpl implements AccountService {
    private final BankRepository bankRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public AccountServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public Account createAccount(String holderName) {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new InvalidAccountName("Invalid Account Name");
        }
        long max = 991599999999999L;
        long min = 991500000000000L;
        long accountNumber = min + (Math.abs(secureRandom.nextLong()) % (max - min + 1));
        Account account = new Account(accountNumber, holderName);
        bankRepository.save(account);
        return account;
    }


    @Override
    public double checkBalance(long accountNumber) {
        Account account = bankRepository.findByAccountNumber(accountNumber);
        if (!AccountUtil.isAccountExists(accountNumber, bankRepository)) {
            throw new AccountNotFoundException("Account Not Found");
        }
        return account.getBalance();
    }


    @Override
    public List<Account> listAccounts() {
        return bankRepository.findAllAccounts()
                .stream()
                .sorted((a1, a2) -> Double.compare(a2.getBalance(), a1.getBalance()))
                .toList();
    }
}
