package com.yuvraj.minibank.controller;

import com.yuvraj.minibank.exception.Account.AccountNotFoundException;
import com.yuvraj.minibank.exception.Account.InvalidAccountName;
import com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.repository.FakeBankRepository;
import com.yuvraj.minibank.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountControllerTest {

    private AccountController controller;

    @BeforeEach
    void setup() {
        FakeBankRepository fakeRepo = new FakeBankRepository();
        AccountServiceImpl accountService = new AccountServiceImpl(fakeRepo);
        controller = new AccountController(accountService);
    }

    @Test
    void testCreateAccountValid() {
        Account acc = controller.createAccount("Yuvraj");
        assertNotNull(acc);
        assertEquals("Yuvraj", acc.getHolderName());
    }

    @Test
    void testCreateAccountInvalidName() {
        assertThrows(InvalidAccountName.class, () -> controller.createAccount(""));
    }

    @Test
    void testCheckBalanceValid() {
        Account acc = controller.createAccount("Rahul");
        double balance = controller.checkBalance(acc.getAccountNumber());
        assertEquals(0.0, balance);
    }

    @Test
    void testCheckBalanceInvalidAccount() {
        assertThrows(AccountNotFoundException.class, () -> controller.checkBalance(99999L));
    }

    @Test
    void testListAccounts() {
        controller.createAccount("A");
        controller.createAccount("B");
        List<Account> accounts = controller.listAccounts();
        assertEquals(2, accounts.size());
    }
}
