package com.yuvraj.minibank.model;


import java.util.ArrayList;
import java.util.List;

public class Account {
    private final long accountNumber;
    private final String holderName;
    private double balance;
    private final List <Transaction> transactions = new ArrayList<>();

    public Account(long accountNumber, String holderName) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = 0;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public  void deposit(double amount) {
        balance += amount;
    }
    public  void withdraw(double amount)
    {
        balance -= amount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
    @Override
    public String toString() {
        return "Account Number: " + accountNumber +
                ", Holder Name: " + holderName +
                ", Balance: " + balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
