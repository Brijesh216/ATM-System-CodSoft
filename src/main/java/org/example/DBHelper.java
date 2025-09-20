package org.example;

import java.sql.*;
import java.util.*;

class DBHelper {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/atmdb";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Fetch account by username
    public static BankAccount getAccount(String username) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new BankAccount(
                        rs.getString("username"),
                        rs.getString("pin"),
                        rs.getDouble("balance"),
                        rs.getDouble("withdrawnToday")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Save new account
    public static void saveAccount(BankAccount acc) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO accounts(username, pin, balance, withdrawnToday) VALUES(?, ?, ?, ?)")) {
            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPin());
            ps.setDouble(3, acc.getBalance());
            ps.setDouble(4, acc.getWithdrawnToday());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update balance & withdrawnToday
    public static void updateAccount(BankAccount acc) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE accounts SET balance=?, withdrawnToday=? WHERE username=?")) {
            ps.setDouble(1, acc.getBalance());
            ps.setDouble(2, acc.getWithdrawnToday());
            ps.setString(3, acc.getUsername());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save transaction
    public static void saveTransaction(String username, String detail) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO transactions(username, detail) VALUES(?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, detail);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch transaction history
    public static List<String> getTransactions(String username) {
        List<String> history = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT detail, timestamp FROM transactions WHERE username=? ORDER BY timestamp DESC LIMIT 10")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(rs.getTimestamp("timestamp") + " - " + rs.getString("detail"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }
}
