package com.yuvraj.minibank.service;
import com.yuvraj.minibank.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryService {
    List<Transaction> getTransactionHistory(long accountNumber);

    List<Transaction> filterByType(long accountNumber, String type);

    List<Transaction> filterByStatus(long accountNumber, String status);

    List<Transaction> filterByAmount(long accountNumber, double min, double max, String condition);

    List<Transaction> filterByTime(long accountNumber, LocalDateTime start, LocalDateTime end);
}