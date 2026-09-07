package com.yuvraj.minibank.service;

import com.yuvraj.minibank.model.Account;

import java.util.List;
import java.util.Map;

public interface ReportsService {

       double getTotalMoney();
       Account getRichestAccount() ;
       List<Account> getAccountsAboveAmount(double amount);
       Map<String, Double> getTotalDepositWithdraw();
}
