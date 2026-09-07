package com.yuvraj.minibank.repository;

import com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.model.Transaction;
import com.yuvraj.minibank.storage.FileStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankRepository {

    private final Map<Long, Account> accounts = new HashMap<>();
    private final FileStorage fileStorage;
    public BankRepository() {
        this.fileStorage = null;
    }

    public BankRepository(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        this.accounts.putAll(fileStorage.loadAccounts());
    }

    public void save(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }
    public Account findByAccountNumber(long accountNumber) {
        return accounts.get(accountNumber);
    }
    public boolean isAccountExists(long accountNumber) {
        return accounts.containsKey(accountNumber);
    }
    public void addTransaction(Account account, Transaction transaction) {
        account.getTransactions().add(transaction);
        updateAccount(account);
    }

    public void updateAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public List<Account> findAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

     public void saveToFile() {
        if (fileStorage != null) {
            fileStorage.saveAccounts(accounts);
        }
    }

    public void exportStatement(long accountNumber) {
        Account account = accounts.get(accountNumber);

        if (account != null) {
            fileStorage.exportStatement(account);
        } else {
            System.out.println("Account not found.");
        }
    }
}