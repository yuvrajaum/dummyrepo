package com.yuvraj.minibank.controller;

import com.yuvraj.minibank.service.DepositService;

public class DepositController {
    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    public void deposit(long accountNumber, double amount) {
        depositService.deposit(accountNumber, amount);
    }


}
