package com.yuvraj.minibank.storage;

import com.yuvraj.minibank.model.Account;
import com.yuvraj.minibank.model.Transaction;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileStorage {
    private static final Pattern TRANSFER_DESCRIPTION_PATTERN =
            Pattern.compile("from Account\\s+(\\d+)\\s+to Account\\s+(\\d+)", Pattern.CASE_INSENSITIVE);

    public void saveAccounts(Map<Long, Account> accounts) {
        File file = new File("data/accounts.txt");
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                System.out.println("Unable to create data folder.");
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Account account : accounts.values()) {
                writer.write("===============================================================");
                writer.newLine();
                writer.write("                     ACCOUNT DETAILS");
                writer.newLine();
                writer.write("===============================================================");
                writer.newLine();

                writer.write("Account Number    : " + account.getAccountNumber());
                writer.newLine();
                writer.write("Account Holder    : " + account.getHolderName());
                writer.newLine();
                writer.write("Current Balance   : " + account.getBalance());
                writer.newLine();
                writer.newLine();

                writer.write("---------------------------------------------------------------");
                writer.newLine();

                writer.write("Transactions:");
                writer.newLine();
                writer.write("---------------------------------------------------------------");
                writer.newLine();

                if (account.getTransactions().isEmpty()) {
                    writer.write("No transactions found");
                    writer.newLine();

                } else {

                    for (Transaction t : account.getTransactions()) {

                        writer.write("Type              : " + t.getType());
                        writer.newLine();

                        writer.write("Amount            : " + t.getAmount());
                        writer.newLine();

                        if ("TRANSFER".equalsIgnoreCase(t.getType())) {
                            writer.write("Sender Account    : " + t.getSenderAccount());
                            writer.newLine();

                            writer.write("Recipient Account : " + t.getRecipientAccount());
                            writer.newLine();
                        }

                        writer.write("Description       : " + t.getDescription());
                        writer.newLine();

                        writer.write("Status            : " + t.getStatus());
                        writer.newLine();

                        writer.write("Time              : " + t.getTimeStamp());
                        writer.newLine();

                        writer.newLine();
                    }
                }

                writer.write("===============================================================");
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Unable to save accounts: " + e.getMessage());
        }
    }
    public Map<Long, Account> loadAccounts() {
        Map<Long, Account> accounts = new HashMap<>();

        File file = new File("data/accounts.txt");

        if (!file.exists()) {
            return accounts;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            Account currentAccount = null;

            String type = null;
            double amount = 0;
            Long senderAccount = null;
            Long recipientAccount = null;
            String description = null;
            String status = null;
            LocalDateTime timeStamp = null;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()
                        || line.startsWith("=")
                        || line.startsWith("-")
                        || line.equals("ACCOUNT DETAILS")
                        || line.equals("Transactions:")) {

                    continue;
                }

                if (line.startsWith("Account Number")) {

                    long accountNumber =
                            Long.parseLong(line.split(":", 2)[1].trim());

                    currentAccount = new Account(accountNumber, "");

                    accounts.put(accountNumber, currentAccount);

                }

                else if (line.startsWith("Account Holder")) {

                    String holderName =
                            line.split(":", 2)[1].trim();

                    if (currentAccount != null) {

                        currentAccount =
                                new Account(
                                        currentAccount.getAccountNumber(),
                                        holderName
                                );

                        accounts.put(
                                currentAccount.getAccountNumber(),
                                currentAccount
                        );
                    }
                }

                else if (line.startsWith("Current Balance")) {

                    double balance =
                            Double.parseDouble(
                                    line.split(":", 2)[1].trim()
                            );

                    if (currentAccount != null) {
                        currentAccount.setBalance(balance);
                    }
                }

                else if (line.startsWith("Type")) {

                    type = line.split(":", 2)[1].trim();
                }

                else if (line.startsWith("Amount")) {

                    amount =
                            Double.parseDouble(
                                    line.split(":", 2)[1].trim()
                            );
                }

                else if (line.startsWith("Sender Account")) {

                    senderAccount =
                            Long.parseLong(
                                    line.split(":", 2)[1].trim()
                            );
                }

                else if (line.startsWith("Recipient Account")) {

                    recipientAccount =
                            Long.parseLong(
                                    line.split(":", 2)[1].trim()
                            );
                }

                else if (line.startsWith("Description")) {

                    description =
                            line.split(":", 2)[1].trim();
                }

                else if (line.startsWith("Status")) {

                    status =
                            line.split(":", 2)[1].trim();
                }

                else if (line.startsWith("Time")) {

                    timeStamp =
                            LocalDateTime.parse(
                                    line.split(":", 2)[1].trim()
                            );

                    if (currentAccount != null
                            && type != null
                            && description != null
                            && status != null) {

                        long txSenderAccount =
                                senderAccount != null
                                        ? senderAccount
                                        : currentAccount.getAccountNumber();
                        long txRecipientAccount =
                                recipientAccount != null
                                        ? recipientAccount
                                        : currentAccount.getAccountNumber();

                        if ("TRANSFER".equalsIgnoreCase(type)) {
                            Matcher matcher = TRANSFER_DESCRIPTION_PATTERN.matcher(description);
                            if (matcher.find()) {
                                long parsedSender = Long.parseLong(matcher.group(1));
                                long parsedRecipient = Long.parseLong(matcher.group(2));
                                if (senderAccount == null
                                        || recipientAccount == null
                                        || txSenderAccount != parsedSender
                                        || txRecipientAccount != parsedRecipient) {
                                    txSenderAccount = parsedSender;
                                    txRecipientAccount = parsedRecipient;
                                }
                            }
                        }

                        Transaction transaction =
                                new Transaction(
                                        status,
                                        type,
                                        amount,
                                        txSenderAccount,
                                        txRecipientAccount,
                                        timeStamp,
                                        description
                                );

                        currentAccount
                                .getTransactions()
                                .add(transaction);
                    }

                    // Reset transaction data
                    type = null;
                    amount = 0;
                    senderAccount = null;
                    recipientAccount = null;
                    description = null;
                    status = null;
                    timeStamp = null;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Unable to load accounts: " + e.getMessage()
            );
        }

        return accounts;
    }
    public void exportStatement(Account account) {
        File folder = new File("statements");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                System.out.println("Unable to create statements folder.");
                return;
            }
        }

        String filePath = "statements/" + account.getAccountNumber() + "_statement.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("===============================================================");
            writer.newLine();
            writer.write("                     YOUR ACCOUNT STATEMENT");
            writer.newLine();
            writer.write("===============================================================");
            writer.newLine();
            writer.newLine();

            writer.write("Account Number    : " + account.getAccountNumber());
            writer.newLine();
            writer.write("Account Holder    : " + account.getHolderName());
            writer.newLine();
            writer.write("Current Balance   : " + account.getBalance());
            writer.newLine();
            writer.write("Statement Date    : " + LocalDateTime.now());
            writer.newLine();
            writer.newLine();

            writer.write("---------------------------------------------------------------");
            writer.newLine();
            writer.write("S.No  Date & Time          Type       Amount   Status     Detail");
            writer.newLine();
            writer.write("---------------------------------------------------------------");
            writer.newLine();

            int index = 1;
            double totalDeposits = 0;
            double totalWithdrawals = 0;

            for (Transaction t : account.getTransactions()) {
                writer.write(String.format(
                        "%-5d %-20s %-9s %-8.2f %-10s %-30s",
                        index++,
                        t.getTimeStamp().toString(),
                        t.getType(),
                        t.getAmount(),
                        t.getStatus(),
                        t.getDescription()
                ));
                writer.newLine();

                if (t.getStatus().equalsIgnoreCase("SUCCESS")) {
                    if (t.getType().equalsIgnoreCase("DEPOSIT")) {
                        totalDeposits += t.getAmount();
                    } else if (t.getType().equalsIgnoreCase("WITHDRAW")) {
                        totalWithdrawals += t.getAmount();
                    }
                }
            }

            writer.write("===============================================================");
            writer.newLine();
            writer.write("                       ACCOUNT SUMMARY");
            writer.newLine();
            writer.write("===============================================================");
            writer.newLine();
            writer.write("Total Deposits      : " + totalDeposits);
            writer.newLine();
            writer.write("Total Withdrawals   : " + totalWithdrawals);
            writer.newLine();
            writer.write("Total Transactions  : " + account.getTransactions().size());
            writer.newLine();
            writer.write("Available Balance   : " + account.getBalance());
            writer.newLine();
            writer.write("===============================================================");
            writer.newLine();

            writer.flush();
        } catch (IOException e) {
            System.out.println("Unable to export statement.");
        }
    }



    public void sendStatementByEmail(Account account, String recipientEmail) {
        exportStatement(account);
        String filePath = "statements/" + account.getAccountNumber() + "_statement.txt";
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Statement file not found.");
            return;
        }

         Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

         final String senderEmail = "yuvraj.mandloi022@gmail.com";
        final String senderPassword = "vesq ijla fvnk xkeu";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your Bank Statement");

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText("Dear " + account.getHolderName() + ",\n\nPlease find attached your bank statement.");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(file);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Statement sent successfully to " + recipientEmail);

        } catch (Exception e) {
            System.out.println("Unable to send email: " + e.getMessage());
        }
    }

}