package com.yuvraj.minibank.service.Impl;

import com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.repository.FakeBankRepository;
import com.yuvraj.minibank.service.impl.DepositServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepositServiceImplTest {

    private DepositServiceImpl depositService;
    private FakeBankRepository fakeRepo;

    @BeforeEach
    void setup() {
        fakeRepo = new FakeBankRepository();
        depositService = new DepositServiceImpl(fakeRepo);
    }

    @Test
    void testDepositValid() {
        Account acc = new Account(101L, "Yuvraj");
        fakeRepo.save(acc);

        depositService.deposit(acc.getAccountNumber(), 500);
        assertEquals(500, fakeRepo.findByAccountNumber(101L).getBalance());
    }

    @Test
    void testDepositMultipleTimes() {
        Account acc = new Account(102L, "TestUser");
        fakeRepo.save(acc);
        depositService.deposit(acc.getAccountNumber(), 200);
        depositService.deposit(acc.getAccountNumber(), 300);

        assertEquals(500, fakeRepo.findByAccountNumber(102L).getBalance());
    }
}
