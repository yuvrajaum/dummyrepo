package com.yuvraj.minibank.service.impl;

import  com.yuvraj.minibank.exception.Account.AccountNotFoundException;
import com.yuvraj.minibank.exception.Transaction.InsufficientFundsException;
import  com.yuvraj.minibank.exception.Transaction.InvalidAmountException;
import com.yuvraj.minibank.exception.Transaction.InvalidInputException;
import com.yuvraj.minibank.exception.Transaction.SameAccountTransferException;
import com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.model.Transaction;
import com.yuvraj.minibank.repository.BankRepository;
import com.yuvraj.minibank.service.TransferService;

import java.time.LocalDateTime;

import static com.yuvraj.minibank.common.Constant.*;

public class TransferServiceImpl implements TransferService {
    private final BankRepository bankRepository;

    public TransferServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public String transfer(long senderAccountNumber, long recipientAccountNumber, double amount) {
        Account senderAccount = bankRepository.findByAccountNumber(senderAccountNumber);
        Account recipientAccount = bankRepository.findByAccountNumber(recipientAccountNumber);

        // Sender account not found
        if (senderAccount == null) {
            throw new AccountNotFoundException("Sender Account Not Found");
        }

        // Recipient account not found
        if (recipientAccount == null) {
            Transaction failedTransaction = new Transaction(
                    FAILED, TRANSFER, amount,
                    senderAccountNumber, recipientAccountNumber,
                    LocalDateTime.now(),
                    "Transfer failed: Recipient Account Not Found"
            );
            bankRepository.addTransaction(senderAccount, failedTransaction);
            throw new AccountNotFoundException("Recipient Account Not Found");
        }

        // Same account transfer
        if (senderAccountNumber == recipientAccountNumber) {
            Transaction failedTransaction = new Transaction(
                    FAILED, TRANSFER, amount,
                    senderAccountNumber, recipientAccountNumber,
                    LocalDateTime.now(),
                    "Transfer failed: Cannot transfer to same account"
            );
            bankRepository.addTransaction(senderAccount, failedTransaction);
            throw new SameAccountTransferException("Cannot transfer to same Account");
        }

        // Invalid amount
        if (amount <= 0) {
            Transaction failedTransaction = new Transaction(
                    FAILED, TRANSFER, amount,
                    senderAccountNumber, recipientAccountNumber,
                    LocalDateTime.now(),
                    "Transfer failed: Invalid Amount"
            );
            bankRepository.addTransaction(senderAccount, failedTransaction);
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        // Insufficient funds
        if (senderAccount.getBalance() < amount) {
            Transaction failedTransaction = new Transaction(
                    FAILED, TRANSFER, amount,
                    senderAccountNumber, recipientAccountNumber,
                    LocalDateTime.now(),
                    "Transfer failed: Insufficient Balance from Account " + senderAccountNumber
            );
            bankRepository.addTransaction(senderAccount, failedTransaction);
            throw new InsufficientFundsException("Insufficient Balance");
        }

        // Success case
        senderAccount.withdraw(amount);
        try {
            recipientAccount.deposit(amount);
        } catch (RuntimeException e) {
            senderAccount.deposit(amount);
            Transaction failedTransaction = new Transaction(
                    FAILED, TRANSFER, amount,
                    senderAccountNumber, recipientAccountNumber,
                    LocalDateTime.now(),
                    "Transfer failed: Credit step failed, amount restored to sender"
            );
            bankRepository.addTransaction(senderAccount, failedTransaction);
            throw new InvalidInputException("Transfer failed during credit. Sender balance restored.");
        }

        Transaction transaction = new Transaction(
                SUCCESS, TRANSFER, amount,
                senderAccountNumber, recipientAccountNumber,
                LocalDateTime.now(),
                "Amount Transferred Successfully from Account " + senderAccountNumber +
                        " to Account " + recipientAccountNumber
        );

        bankRepository.addTransaction(senderAccount, transaction);
        bankRepository.addTransaction(recipientAccount, transaction);

        return String.format("\n%.2f transferred successfully from %s to %s",
                amount, senderAccount.getHolderName(), recipientAccount.getHolderName());
    }
}
