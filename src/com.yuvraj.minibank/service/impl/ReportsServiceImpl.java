package com.yuvraj.minibank.service.impl;

import  com.yuvraj.minibank.exception.Account.AccountNotFoundException;
import  com.yuvraj.minibank.model.Account;
import  com.yuvraj.minibank.model.Transaction;
import  com.yuvraj.minibank.repository.BankRepository;
import  com.yuvraj.minibank.service.ReportsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsServiceImpl implements ReportsService {

    private final BankRepository bankRepository;

    public ReportsServiceImpl( BankRepository bankRepository){
        this.bankRepository =bankRepository;
    }


    @Override
    public  double getTotalMoney() {
        List<Account> accounts =bankRepository.findAllAccounts();
         double total = accounts.stream()
                 .mapToDouble(Account::getBalance)
                 .sum();
                 return total;

    }

    @Override
    public Account getRichestAccount() {
        return bankRepository.findAllAccounts()
                .stream().min((a1, a2) -> Double.compare(a2.getBalance(), a1.getBalance()))
                .orElseThrow(() ->
                        new AccountNotFoundException("No accounts in bank."));
    }

    @Override
    public List<Account> getAccountsAboveAmount(double amount){
        return bankRepository.findAllAccounts()
                .stream()
                .filter(acc ->acc.getBalance() >= amount)
                .toList();
    }



    @Override
    public Map<String ,Double> getTotalDepositWithdraw(){
        List<Account> accounts = bankRepository.findAllAccounts();

         double totalDeposit = accounts.stream()
                 .flatMap(account -> account.getTransactions().stream())
                 .filter(t -> t.getType().equalsIgnoreCase("DEPOSIT"))
                 .mapToDouble(Transaction::getAmount)
                 .sum();
        double totalWithdraw = accounts.stream()
                .flatMap(account -> account.getTransactions().stream())
                .filter(t -> t.getType().equalsIgnoreCase("WITHDRAW"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        Map<String,Double> result = new HashMap<>();
        result.put("deposit",totalDeposit);
        result.put("withdraw",totalWithdraw);
        return result;

            }
}
