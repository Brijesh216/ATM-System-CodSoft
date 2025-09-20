package org.example;

import java.util.*;

class BankAccount {
    private String username;
    private String pin;
    private double balance;
    private double dailyWithdrawLimit = 10000;
    private double withdrawnToday;

    public BankAccount(String username, String pin, double balance, double withdrawnToday) {
        this.username = username;
        this.pin = pin;
        this.balance = balance;
        this.withdrawnToday = withdrawnToday;
    }

    public String getUsername() { return username; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    public double getWithdrawnToday() { return withdrawnToday; }

    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            DBHelper.updateAccount(this);
            DBHelper.saveTransaction(username, "Deposited: " + amount);
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (amount > balance) {
            DBHelper.saveTransaction(username, "Failed withdrawal: " + amount + " (Insufficient Balance)");
            return false;
        }
        if (withdrawnToday + amount > dailyWithdrawLimit) {
            DBHelper.saveTransaction(username, "Failed withdrawal: " + amount + " (Limit Exceeded)");
            return false;
        }
        balance -= amount;
        withdrawnToday += amount;
        DBHelper.updateAccount(this);
        DBHelper.saveTransaction(username, "Withdrawn: " + amount);
        return true;
    }

    public List<String> getTransactionHistory() {
        return DBHelper.getTransactions(username);
    }
}
