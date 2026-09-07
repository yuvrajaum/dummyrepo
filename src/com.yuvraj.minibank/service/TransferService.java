package com.yuvraj.minibank.service;

public interface TransferService {
    String transfer(long senderAccountNumber ,long recipientAccountNumber, double amount);
}
