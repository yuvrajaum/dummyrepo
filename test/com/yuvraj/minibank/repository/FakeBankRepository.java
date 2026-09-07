
package com.yuvraj.minibank.repository;

import com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Test ke liye in-memory repo
public class FakeBankRepository extends BankRepository {

    private final Map<Long, Account> accounts = new HashMap<>();

    public FakeBankRepository() {
        super(); // yeh default constructor call karega, fileStorage = null set hoga
    }

    @Override
    public void save(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    @Override
    public Account findByAccountNumber(long accountNumber) {
        return accounts.get(accountNumber);
    }

    @Override
    public boolean isAccountExists(long accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    @Override
    public void addTransaction(Account account, Transaction transaction) {
        account.getTransactions().add(transaction);
        updateAccount(account);
    }

    @Override
    public void updateAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    @Override
    public List<Account> findAllAccounts() {
        return new ArrayList<>(accounts.values());
    }
}
