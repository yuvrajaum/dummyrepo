package com.yuvraj.minibank;

import com.yuvraj.minibank.controller.*;
import com.yuvraj.minibank.exception.Account.AccountNotFoundException;
import com.yuvraj.minibank.exception.Account.InvalidAccountName;
import com.yuvraj.minibank.exception.Transaction.InsufficientFundsException;
import com.yuvraj.minibank.exception.Transaction.InvalidAmountException;
import com.yuvraj.minibank.exception.Transaction.InvalidInputException;
import com.yuvraj.minibank.exception.Transaction.SameAccountTransferException;
import com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.model.Transaction;
import com.yuvraj.minibank.repository.BankRepository;
import com.yuvraj.minibank.service.impl.*;
import com.yuvraj.minibank.storage.FileStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner sc;
    private static BankRepository repo;
    private static AccountController accountController;
    private static DepositController depositController;
    private static WithdrawController withdrawController;
    private static TransferController transferController;
    private static TransactionHistoryController transactionHistoryController;
    private static ReportsController reportsController;

    public static void initialize() {
        FileStorage file= new FileStorage();
        repo = new BankRepository(file);
        accountController = new AccountController(new AccountServiceImpl(repo));
        depositController = new DepositController(new DepositServiceImpl(repo));
        withdrawController = new WithdrawController(new WithdrawServiceImpl(repo));
        transferController = new TransferController(new TransferServiceImpl(repo));
        transactionHistoryController = new TransactionHistoryController(new TransactionHistoryServiceImpl(repo));
        reportsController = new ReportsController(new ReportsServiceImpl(repo));
    }
    public static void setScanner() {
        sc = new Scanner(System.in);
    }

    public static void main(String[] args) {
        initialize();
        setScanner();

        while (true) {
            showMenu();
            int choice = readChoice();
            switch (choice) {
                case 1 -> handleCreateAccount();
                case 2 -> handleDeposit();
                case 3 -> handleCheckBalance();
                case 4 -> handleWithdraw();
                case 5 -> handleListAccounts();
                case 6 -> handleTransfer();
                case 7 -> handleTransactionHistory();
                case 8 -> handleReports();
                case 9 ->handleExportStatement();
                case 0 -> {
                    repo.saveToFile();
                    System.out.println("Thank you");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice, inds please " +
                        " the select from menu.");
            }
        }
    }

    public static void showMenu() {
        System.out.println("\n===== Welcome to MiniBank =====\n");
        System.out.println("1. Create account");
        System.out.println("2. Deposit Amount");
        System.out.println("3. Check Balance");
        System.out.println("4. Withdraw Amount");
        System.out.println("5. Account List");
        System.out.println("6. Transfer Amount");
        System.out.println("7. Transaction History");
        System.out.println("8. Reports");
        System.out.println("9. Export Statements");
        System.out.println("0. Ex  it\n");
    }

    private static String readInput(String prompt, String regex) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                if (!input.matches(regex)) {
                    if (prompt.contains("Name")) {
                        throw new InvalidAccountName("Name must contain only letters .");
                    } else if (prompt.contains("Account Number")) {
                        throw new InvalidInputException("Account number must contain only digits.");
                    } else if (prompt.contains("Amount")) {
                        throw new InvalidAmountException("Amount must be numeric.");
                    }
                    else if (prompt.contains("Date")) {
                        throw new InvalidInputException(
                                "Invalid date format. Please enter in dd-MM-yyyy format (e.g., 19-08-2026)");
                    }else {
                        throw new InvalidInputException("Invalid input, please try again.");
                    }
                }
                return input;
            } catch (InvalidAccountName | InvalidAmountException | InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static int readChoice() {
        while (true) {
            try {
                String choice = readInput("Choose an option : ", "\\d");
                return Integer.parseInt(choice);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number for choice.");
            }
        }
    }

    public static void handleCreateAccount() {
        try {
            String name = readInput("Enter Name : ", "^[A-Za-z]+( [A-Za-z]+)*$");
            if (name.length() < 3) {
                throw new InvalidAccountName("Name must be at least 3 characters long.");
            }
            Account account = accountController.createAccount(name);

            System.out.println("\n==== Account created ===\n");
            System.out.println("Account Number : " + account.getAccountNumber());
            System.out.println("Holder Name    : " + account.getHolderName());
            System.out.println("Balance        : " + account.getBalance());
        } catch (InvalidAccountName e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void handleDeposit() {
        try {
            long accountNumber = Long.parseLong(readInput("Enter Account Number : ", "\\d+"));
            double amount = Double.parseDouble(readInput("Enter Deposit Amount : ", "^-?\\d+(\\.\\d+)?$"));
            depositController.deposit(accountNumber, amount);
            System.out.println("Deposit Successful!");

        } catch (AccountNotFoundException | InvalidAmountException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Please enter numbers only.");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void handleCheckBalance() {
        try {
            long accountNumber = Long.parseLong(readInput("Enter Account Number : ", "\\d+"));
            double balance = accountController.checkBalance(accountNumber);
            System.out.println("Current Balance : " + balance);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid account number.");
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void handleWithdraw() {
        try {
            long accountNumber = Long.parseLong(readInput("Enter Account Number : ", "\\d+"));
            double amount = Double.parseDouble(readInput("Enter Withdraw  Amount : ", "^-?\\d+(\\.\\d+)?$"));
            withdrawController.withdraw(accountNumber, amount);
            System.out.println("Withdraw  Successful");

        } catch (AccountNotFoundException | InsufficientFundsException | InvalidAmountException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Please enter numbers only.");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void handleListAccounts() {
        try {
            List<Account> accounts = accountController.listAccounts();
            if (accounts.isEmpty()) {
                System.out.println("No Accounts found.");
            } else {
                System.out.printf("%-18s %-20s %-10s%n", "Account Number", "Holder Name", "Balance");
                for (Account acc : accounts) {
                    System.out.printf("%-18d %-20s %-10.2f%n", acc.getAccountNumber(), acc.getHolderName(), acc.getBalance());
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected error : " + e.getMessage());
        }
    }


    public static void handleTransfer() {
        try {
            long senderAccountNumber = Long.parseLong(readInput("Enter Sender Account Number : ", "\\d+"));
            long recipientAccountNumber = Long.parseLong(readInput("Enter Recipient Account Number : ", "\\d+"));
            long CheckRecipientAccountNumber = Long.parseLong(readInput("Confirm Recipient Account Number : ", "\\d+"));
            if(CheckRecipientAccountNumber == recipientAccountNumber){
            double amount = Double.parseDouble(readInput("Enter Amount : ", "^-?\\d+(\\.\\d+)?$"));
            String message = transferController.transfer(senderAccountNumber, recipientAccountNumber, amount);
            System.out.println(message);
            }
            else{
                System.out.println("Confirmation Account Number Is Not same");
            }
        } catch (SameAccountTransferException | InvalidAmountException | InsufficientFundsException |
                 AccountNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error " + e.getMessage());
        }
    }

    public static void handleTransactionHistory() {
        try {
            long accountNumber = Long.parseLong(readInput("Enter Account Number : ", "\\d+"));
            while (true) {
                if (accountNumber == 0) {
                    System.out.println("Returning to main.Main Menu...");
                    return;
                }
                System.out.println("\n===== Transaction History Options =====");
                System.out.println("1. Show All Transactions");
                System.out.println("2. Filter by Type");
                System.out.println("3. Filter by Status");
                System.out.println("4. Filter by Amount");
                System.out.println("5. Filter by Time");
                System.out.println("0. Exit To Main Menu");
                int choice = Integer.parseInt(readInput("Choose option : ", "\\d"));
                List<Transaction> filtered = List.of();
                boolean skipDisplay = false;
                switch (choice) {
                    case 1 -> filtered = transactionHistoryController.getTransactionHistory(accountNumber);
                    case 2 -> {
                        while (true) {
                            System.out.println("\n===== Filter By Type =====");
                            System.out.println("1. DEPOSIT");
                            System.out.println("2. WITHDRAW");
                            System.out.println("3. TRANSFER");
                            System.out.println("0. Exit");
                            int typeChoice = Integer.parseInt(readInput("Choose Type : ", "\\d"));
                            if (typeChoice == 0) {
                                skipDisplay = true;
                                break;
                            }
                            String type;
                            switch (typeChoice) {
                                case 1 -> type = "DEPOSIT";
                                case 2 -> type = "WITHDRAW";
                                case 3 -> type = "TRANSFER";
                                default -> {
                                    System.out.println("Invalid choice, please try again.");
                                    continue;
                                }
                            }
                            filtered = transactionHistoryController.filterByType(accountNumber, type);
                            if (filtered.isEmpty()) {
                                System.out.println("No transactions found for type: " + type);
                                continue;
                            }
                            System.out.println("\n========================================== TRANSACTION HISTORY ==========================================================\n");
                            System.out.printf("%-12s %-18s %-20s %-20s %-35s%n", "Type", "Amount", "Date & Time", "Status", "Description");
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                            for (Transaction t : filtered) {
                                System.out.printf("%-12s %-18.2f %-20s %-20s %-35s%n", t.getType(), t.getAmount(), t.getTimeStamp().withNano(0), t.getStatus(), t.getDescription());
                            }
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                        }
                    }
                    case 3 -> {
                        while (true) {
                            System.out.println("\n===== Filter By Status =====");
                            System.out.println("1. SUCCESS");
                            System.out.println("2. FAILED");
                            System.out.println("0. Exit");
                            int statusChoice = Integer.parseInt(readInput("Choose Status : ", "\\d"));
                            if (statusChoice == 0) {
                                skipDisplay = true;
                                break;
                            }
                            String status;
                            switch (statusChoice) {
                                case 1 -> status = "SUCCESS";
                                case 2 -> status = "FAILED";
                                default -> {
                                    System.out.println("Invalid choice, please try again.");
                                    continue;
                                }
                            }
                            filtered = transactionHistoryController.filterByStatus(accountNumber, status);
                            if (filtered.isEmpty()) {
                                System.out.println("No transactions found with status: " + status);
                                continue;
                            }
                            System.out.println("\n========================================== TRANSACTION HISTORY ==========================================================\n");
                            System.out.printf("%-12s %-18s %-20s %-20s %-35s%n", "Type", "Amount", "Date & Time", "Status", "Description");
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                            for (Transaction t : filtered) {
                                System.out.printf("%-12s %-18.2f %-20s %-20s %-35s%n", t.getType(), t.getAmount(), t.getTimeStamp().withNano(0), t.getStatus(), t.getDescription());
                            }
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                        }
                    }
                    case 4 -> {
                        while (true) {
                            System.out.println("\n===== Filter By Amount =====");
                            System.out.println("1. More than");
                            System.out.println("2. Less than");
                            System.out.println("3. Between Range");
                            System.out.println("0. Exit");
                            int amountChoice = Integer.parseInt(readInput("Enter choice : ", "\\d"));
                            if (amountChoice == 0) {
                                skipDisplay = true;
                                break;
                            }
                            if (amountChoice == 1) {
                                double min = Double.parseDouble(readInput("Enter Amount : ", "\\d+(\\.\\d+)?"));
                                filtered = transactionHistoryController.filterByAmount(accountNumber, min, 0, "MORE");
                            } else if (amountChoice == 2) {
                                double max = Double.parseDouble(readInput("Enter Amount : ", "\\d+(\\.\\d+)?"));
                                filtered = transactionHistoryController.filterByAmount(accountNumber, 0, max, "LESS");
                            } else if (amountChoice == 3) {
                                double min = Double.parseDouble(readInput("Enter Min Amount : ", "\\d+(\\.\\d+)?"));
                                double max = Double.parseDouble(readInput("Enter Max Amount : ", "\\d+(\\.\\d+)?"));
                                filtered = transactionHistoryController.filterByAmount(accountNumber, min, max, "BETWEEN");
                            } else {
                                System.out.println("Invalid choice, please try again.");
                                continue;
                            }
                            if (filtered.isEmpty()) {
                                System.out.println("No transactions found.");
                                continue;
                            }
                            System.out.println("\n========================================== TRANSACTION HISTORY ==========================================================\n");
                            System.out.printf("%-12s %-18s %-20s %-20s %-35s%n", "Type", "Amount", "Date & Time", "Status", "Description");
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                            for (Transaction t : filtered) {
                                System.out.printf("%-12s %-18.2f %-20s %-20s %-35s%n", t.getType(), t.getAmount(), t.getTimeStamp().withNano(0), t.getStatus(), t.getDescription());
                            }
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                        }
                    }
                    case 5 -> {
                        while (true) {
                            System.out.println("\n===== Filter By Time =====");
                            System.out.println("1. Today");
                            System.out.println("2. Last 7 days");
                            System.out.println("3. Between Dates");
                            System.out.println("0. Exit");
                            int timeChoice = Integer.parseInt(readInput("Choose choice : ", "\\d"));
                            if (timeChoice == 0) {
                                skipDisplay = true;
                                break;
                            }
                            LocalDateTime start, end;

                            if (timeChoice == 1) {
                                LocalDate today = LocalDate.now();
                                start = today.atStartOfDay();
                                end = today.atTime(23, 59, 59);
                            } else if (timeChoice == 2) {
                                start = LocalDateTime.now().minusDays(7);
                                end = LocalDateTime.now();
                            } else if (timeChoice == 3) {

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                                LocalDate sDate = null, eDate = null;
                                boolean validDates = false;
                                while (!validDates) {
                                    try {
                                        String sDateStr = readInput("Enter Start Date (dd-MM-yyyy): ", "\\d{2}-\\d{2}-\\d{4}");
                                        String eDateStr = readInput("Enter End Date (dd-MM-yyyy): ", "\\d{2}-\\d{2}-\\d{4}");
                                        sDate = LocalDate.parse(sDateStr, formatter);
                                        eDate = LocalDate.parse(eDateStr, formatter);

                                        if (sDate.isAfter(eDate)) {
                                            System.out.println("Invalid: Start date must be before end date");
                                            continue;
                                        }
                                        validDates = true;
                                    } catch (Exception e) {
                                        System.out.println("Invalid date format or values. Please enter in dd-MM-yyyy format (e.g., 19-08-2026)");
                                    }
                                }
                                start = sDate.atStartOfDay();
                                end = eDate.atTime(23, 59, 59);
                            } else {
                                System.out.println("Invalid choice, please try again.");
                                continue;
                            }
                            filtered = transactionHistoryController.filterByTime(accountNumber, start, end);
                            if (filtered.isEmpty()) {
                                System.out.println("\n No transactions found in the selected date range.");
                                continue;
                            }

                            System.out.println("\n========================================== TRANSACTION HISTORY ==========================================================\n");
                            System.out.printf("%-12s %-18s %-20s %-20s %-35s%n", "Type", "Amount", "Date & Time", "Status", "Description");
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");

                            for (Transaction t : filtered) {
                                System.out.printf("%-12s %-18.2f %-20s %-20s %-35s%n", t.getType(), t.getAmount(), t.getTimeStamp().withNano(0), t.getStatus(), t.getDescription());
                            }
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                        }
                    }
                    case 0 -> {
                        return;
                    }
                    default -> {
                        System.out.println("Invalid choice, please select from menu.");
                        continue;
                    }
                }
                if (skipDisplay) {
                    continue;
                }

                if (filtered.isEmpty()) {
                    System.out.println("No transactions found.");
                    continue;
                }
                System.out.println("\n========================================== TRANSACTION HISTORY ==========================================================\n");
                System.out.printf("%-12s %-18s %-20s %-20s %-35s%n", "Type", "Amount", "Date & Time", "Status", "Description");
                System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                for (Transaction t : filtered) {
                    System.out.printf("%-12s %-18.2f %-20s %-20s %-35s%n", t.getType(), t.getAmount(), t.getTimeStamp().withNano(0), t.getStatus(), t.getDescription());
                }
                System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void handleReports() {
        try {
            double amount = Double.parseDouble(readInput("Enter Amount: ", "\\d+(\\.\\d+)?"));

            System.out.println("\n================ BANK REPORT ================\n");
            // 1. Total Money
            double totalMoney = reportsController.getTotalMoney();
            System.out.printf("Total Money in Bank : %.2f%n", totalMoney);

            // 2. Richest Account
            Account account = reportsController.getRichestAccount();
            System.out.println("\n--- Richest Account ---");
            System.out.printf("Account Number : %d || Holder : %-10s || Balance : %.2f%n",
                    account.getAccountNumber(), account.getHolderName(), account.getBalance());

            // 3. Accounts Above Amount
            List<Account> accounts = reportsController.getAccountsAboveAmount(amount);
            System.out.println("\n--- Accounts with Balance >= " + amount + " ---");
            if (accounts.isEmpty()) {
                System.out.println("No accounts found above " + amount);
            } else {
                accounts.forEach(acc -> System.out.printf(
                        "Account Number : %d || Holder : %-10s || Balance : %.2f%n",
                        acc.getAccountNumber(), acc.getHolderName(), acc.getBalance()
                ));
            }

            Map<String, Double> result = reportsController.getTotalDepositWithDraw();
            System.out.println("\n--- Deposits vs Withdrawals ---");
            System.out.printf("Total Deposit  : %.2f || Total Withdraw : %.2f%n",
                    result.get("deposit"), result.get("withdraw"));
            System.out.println("\n=============================================\n");
        } catch (InvalidAmountException | AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void handleExportStatement() {
        try {
            long accountNumber = Long.parseLong(readInput("Enter Account Number : ", "\\d+"));
            accountController.checkBalance(accountNumber);

            while (true) {
                System.out.println("1. View Statement");
                System.out.println("2. Download Statement (Email)");
                System.out.println("0. Exit to Main Menu");

                String choiceInput = readInput("Choose option : ", "\\d+"); // allow digits only
                int choice;

                try {
                    choice = Integer.parseInt(choiceInput);
                } catch (Exception e) {
                    //  regex fail ya parse fail dono par same message
                    System.out.println("Invalid Choice. Please enter 0, 1 or 2.");
                    continue;
                }

                if (choice == 0) {
                    System.out.println("Returning to Main Menu...");
                    break;
                } else if (choice == 1) {
                    repo.exportStatement(accountNumber);
                    System.out.println("Statement file created successfully.");
                } else if (choice == 2) {
                    repo.exportStatement(accountNumber);
                    String email = readInput("Enter Email ID : ", "^[A-Za-z0-9+_.-]+@(.+)$");
                    FileStorage storage = new FileStorage();
                    storage.sendStatementByEmail(repo.findByAccountNumber(accountNumber), email);
                } else {
                     System.out.println("Invalid Choice. Please enter 0, 1 or 2.");
                }
            }

        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }



}
