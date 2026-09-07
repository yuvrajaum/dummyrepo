package com.yuvraj.minibank.controller;

import com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.service.ReportsService;

import java.util.List;
import java.util.Map;

public class ReportsController {

    private ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    public double getTotalMoney() {
        return reportsService.getTotalMoney();
    }

    public Account getRichestAccount() {
        return reportsService.getRichestAccount();
    }

    public List<Account> getAccountsAboveAmount(double amount) {
        return reportsService.getAccountsAboveAmount(amount);
    }

    public Map<String, Double> getTotalDepositWithDraw() {
        return reportsService.getTotalDepositWithdraw();
    }


}
