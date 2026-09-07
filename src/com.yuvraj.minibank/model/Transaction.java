package com.yuvraj.minibank.model;

import java.time.LocalDateTime;

public class Transaction {

    private final long senderAccount;
    private final long recipientAccount;
    private final String type;
    private final double amount;
    private  final LocalDateTime TimeStamp;
    private  final String description;
    private   final String status;

    public Transaction(String status, String type, double amount, long senderAccount, long recipientAccount, LocalDateTime TimeStamp, String description) {
        this.senderAccount = senderAccount;
        this.recipientAccount = recipientAccount;
        this.type = type;
        this.amount = amount;
        this.TimeStamp = TimeStamp;
        this.description = description;
        this.status = status;
    }
    public String getType() {
        return type;
    }
    public double getAmount() {
        return amount;
    }
    public LocalDateTime getTimeStamp() {
        return TimeStamp;
    }
    public String getDescription() {
        return description;
    }
    public String getStatus() {
        return status;
    }

    public long getSenderAccount() {
        return senderAccount;
    }
    public long getRecipientAccount() {
        return recipientAccount;
    }




}
