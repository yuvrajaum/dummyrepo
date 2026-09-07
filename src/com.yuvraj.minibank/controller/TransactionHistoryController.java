package com.yuvraj.minibank.controller;

import com.yuvraj.minibank.model.Transaction;
import com.yuvraj.minibank.service.TransactionHistoryService;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionHistoryController {

    private final TransactionHistoryService historyService;

    public TransactionHistoryController(TransactionHistoryService historyService) {
        this.historyService = historyService;
    }

    public List<Transaction> getTransactionHistory(long accountNumber) {
        return historyService.getTransactionHistory(accountNumber);
    }

    public List<Transaction> filterByType(long accountNumber, String type) {
        return historyService.filterByType(accountNumber, type);
    }

    public List<Transaction> filterByStatus(long accountNumber, String status) {
        return historyService.filterByStatus(accountNumber, status);
    }

    public List<Transaction> filterByAmount(long accountNumber, double min, double max, String condition) {
        return historyService.filterByAmount(accountNumber, min, max, condition);
    }

    public List<Transaction> filterByTime(long accountNumber, LocalDateTime start, LocalDateTime end) {
        return historyService.filterByTime(accountNumber, start, end);
    }

}
