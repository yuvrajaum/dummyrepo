package com.yuvraj.minibank.service.impl;

import  com.yuvraj.minibank.exception.Account.AccountNotFoundException;
import com.yuvraj.minibank.exception.Transaction.InvalidAmountException;
import com.yuvraj.minibank.model.Account;
import  com.yuvraj.minibank.model.Transaction;
import  com.yuvraj.minibank.repository.BankRepository;
import  com.yuvraj.minibank.service.DepositService;
import com.yuvraj.minibank.util.AccountUtil;

import java.time.LocalDateTime;

import static  com.yuvraj.minibank.common.Constant.*;

public class DepositServiceImpl implements DepositService {
    private final BankRepository bankRepository;

    public DepositServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public void deposit(long accountNumber, double amount) {
        Account account = bankRepository.findByAccountNumber(accountNumber);
        if (!AccountUtil.isAccountExists(accountNumber, bankRepository)) {
            throw new AccountNotFoundException("Account Not Found");
        }

        if (amount <= 0) {
            Transaction transaction = new Transaction(
                    FAILED,
                    DEPOSIT,
                    amount,
                    accountNumber,
                    accountNumber,
                    LocalDateTime.now(),
                    "Deposit Failed: Invalid Amount"
            );
            bankRepository.addTransaction(account, transaction);
            throw new InvalidAmountException("Invalid Amount");
        }

        account.deposit(amount);
        Transaction transaction = new Transaction(
                SUCCESS,
                DEPOSIT,
                amount,
                accountNumber,
                accountNumber,
                LocalDateTime.now(),
                "Deposit Success"
        );
        bankRepository.addTransaction(account, transaction);
    }


}
