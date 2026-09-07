package com.yuvraj.minibank.service.impl;

import com.yuvraj.minibank.exception.Account.AccountNotFoundException;
import com.yuvraj.minibank.exception.Transaction.InsufficientFundsException;
import com.yuvraj.minibank.exception.Transaction.InvalidAmountException;
import com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.model.Transaction;
import com.yuvraj.minibank.repository.BankRepository;
import com.yuvraj.minibank.service.WithdrawService;
import com.yuvraj.minibank.util.AccountUtil;

import java.time.LocalDateTime;

import static com.yuvraj.minibank.common.Constant.*;

public class WithdrawServiceImpl implements WithdrawService {
    private final BankRepository bankRepository;

    public WithdrawServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }
    @Override
    public void withdraw(long accountNumber, double amount) {
        if (!AccountUtil.isAccountExists(accountNumber, bankRepository)) {
            throw new AccountNotFoundException("Account Not Found");
        }

        Account account = bankRepository.findByAccountNumber(accountNumber);

        if (amount <= 0) {
            Transaction transaction = new Transaction(
                    FAILED,
                    WITHDRAW,
                    amount,
                    accountNumber,
                    0,
                    LocalDateTime.now(),
                    "Withdraw Failed:Invalid Amount"
            );
            bankRepository.addTransaction(account, transaction);
             throw new InvalidAmountException("Invalid Amount");
        }

        if (account.getBalance() < amount) {
            Transaction transaction = new Transaction(
                    FAILED,
                    WITHDRAW,
                    amount,
                    accountNumber,
                    0,
                    LocalDateTime.now(),
                    "Withdraw Failed: Insufficient Balance"
            );
            bankRepository.addTransaction(account, transaction);
             throw new InsufficientFundsException("Insufficient Balance");
        }

        account.withdraw(amount);

        Transaction transaction = new Transaction(
                SUCCESS,
                WITHDRAW,
                amount,
                accountNumber,
                0,
                LocalDateTime.now(),
                "Withdraw Success"
        );
        bankRepository.addTransaction(account, transaction);
    }

}
