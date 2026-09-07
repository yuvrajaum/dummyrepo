package com.yuvraj.minibank.controller;

import com.yuvraj.minibank.service.WithdrawService;

public class WithdrawController {
    private final WithdrawService withdrawService;

    public WithdrawController(WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }

    public void withdraw(long accountNumber, double amount) {
        withdrawService.withdraw(accountNumber, amount);
    }


}
