package com.yuvraj.minibank.service.impl;

import  com.yuvraj.minibank.exception.Account.AccountNotFoundException;
import  com.yuvraj.minibank.model.Account;
import  com.yuvraj.minibank.model.Transaction;
import  com.yuvraj.minibank.repository.BankRepository;
import  com.yuvraj.minibank.service.TransactionHistoryService;
import com.yuvraj.minibank.util.AccountUtil;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final BankRepository bankRepository;

    public TransactionHistoryServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public List<Transaction> getTransactionHistory(long accountNumber) {
        Account account = bankRepository.findByAccountNumber(accountNumber);
        if (!AccountUtil.isAccountExists(accountNumber, bankRepository)) {
            throw new AccountNotFoundException("Account Not Found");
        }        return account.getTransactions();
    }

    @Override
    public List<Transaction> filterByType(long accountNumber, String type) {
        Account account = bankRepository.findByAccountNumber(accountNumber);
        if (account == null) throw new AccountNotFoundException("Account Not Found");

        return account.getTransactions()
                .stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .toList();
    }

    @Override
    public List<Transaction> filterByStatus(long accountNumber, String status) {
        Account account = bankRepository.findByAccountNumber(accountNumber);
        if (!AccountUtil.isAccountExists(accountNumber, bankRepository)) {
            throw new AccountNotFoundException("Account Not Found");
        }
        return account.getTransactions()
                .stream()
                .filter(t -> t.getStatus().equalsIgnoreCase(status))
                .toList();
    }

    @Override
    public List<Transaction> filterByAmount(long accountNumber, double min, double max, String condition) {
        Account account = bankRepository.findByAccountNumber(accountNumber);
        if (!AccountUtil.isAccountExists(accountNumber, bankRepository)) {
            throw new AccountNotFoundException("Account Not Found");
        }
        return account.getTransactions()
                .stream()
                .filter(t -> switch (condition.toUpperCase()) {
                    case "MORE" -> t.getAmount() >= min;
                    case "LESS" -> t.getAmount() < max;
                    case "BETWEEN" -> t.getAmount() >= min && t.getAmount() <= max;
                    default -> false;
                })
                .toList();
    }

    @Override
    public List<Transaction> filterByTime(long accountNumber, LocalDateTime start, LocalDateTime end) {
        Account account = bankRepository.findByAccountNumber(accountNumber);
        if (account == null) throw new AccountNotFoundException("Account Not Found");

        return account.getTransactions()
                .stream()
                .filter(t -> !t.getTimeStamp().isBefore(start) && !t.getTimeStamp().isAfter(end))
                .toList();
    }
}
