package com.yuvraj.minibank.controller;

import com.yuvraj.minibank.service.TransferService;


public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    public String transfer(long senderAccountNumber, long recipientAccountNumber, double amount) {
        return transferService.transfer(senderAccountNumber, recipientAccountNumber, amount);
    }

}
