package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class ATM_GUI extends JFrame {
    private BankAccount currentAccount;
    private JTextField userField, pinField, amountField;

    //  Colors
    private final Color primaryColor = new Color(52, 152, 219);   // Blue
    private final Color accentColor = new Color(46, 204, 113);    // Green
    private final Color dangerColor = new Color(231, 76, 60);     // Red
    private final Font mainFont = new Font("Segoe UI", Font.PLAIN, 16);

    public ATM_GUI() {
        setTitle("ATM Machine");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showLoginScreen();
    }

    // 🔹 Custom Button Factory
    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(mainFont);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // 🔹 Login Screen
    private void showLoginScreen() {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Welcome to ATM", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(primaryColor);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("👤 Username:"), gbc);
        gbc.gridx = 1;
        userField = new JTextField(15);
        panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("🔑 PIN:"), gbc);
        gbc.gridx = 1;
        pinField = new JPasswordField(15);
        panel.add(pinField, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JButton loginBtn = createButton("Login", primaryColor);
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String pin = pinField.getText();
            BankAccount acc = DBHelper.getAccount(username);

            if (acc != null && acc.validatePin(pin)) {
                currentAccount = acc;
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid Username or PIN!");
            }
        });

        add(panel);
        revalidate();
        repaint();
    }

    // 🔹 Main Menu
    private void showMainMenu() {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new GridLayout(6, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("ATM Main Menu", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(primaryColor);

        JButton withdrawBtn = createButton("Withdraw", dangerColor);
        JButton depositBtn = createButton("Deposit", accentColor);
        JButton balanceBtn = createButton("Check Balance", primaryColor);
        JButton historyBtn = createButton("Mini Statement", new Color(155, 89, 182));
        JButton exitBtn = createButton("Exit", Color.DARK_GRAY);

        withdrawBtn.addActionListener(e -> showTransaction("Withdraw"));
        depositBtn.addActionListener(e -> showTransaction("Deposit"));
        balanceBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "💰 Balance: " + currentAccount.getBalance())
        );
        historyBtn.addActionListener(e -> showHistory());
        exitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "👋 Thank you for using ATM!");
            System.exit(0);
        });

        panel.add(header);
        panel.add(withdrawBtn);
        panel.add(depositBtn);
        panel.add(balanceBtn);
        panel.add(historyBtn);
        panel.add(exitBtn);

        add(panel);
        revalidate();
        repaint();
    }

    // 🔹 Transaction Screen
    private void showTransaction(String type) {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Enter Amount to " + type + ":");
        label.setFont(mainFont);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(label, gbc);

        amountField = new JTextField(15);
        gbc.gridy++;
        panel.add(amountField, gbc);

        JButton actionBtn = createButton(type, type.equals("Withdraw") ? dangerColor : accentColor);
        gbc.gridy++;
        panel.add(actionBtn, gbc);

        actionBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (type.equals("Withdraw")) {
                    if (currentAccount.withdraw(amount)) {
                        JOptionPane.showMessageDialog(this, "✅ Withdraw Successful!");
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Insufficient Balance or Limit Exceeded!");
                    }
                } else {
                    currentAccount.deposit(amount);
                    JOptionPane.showMessageDialog(this, "✅ Deposit Successful!");
                }
                showMainMenu();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid Amount!");
            }
        });

        add(panel);
        revalidate();
        repaint();
    }

    // 🔹 Mini Statement
    private void showHistory() {
        List<String> history = currentAccount.getTransactionHistory();
        JTextArea area = new JTextArea();
        for (String record : history) {
            area.append(record + "\n");
        }
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(450, 250));
        JOptionPane.showMessageDialog(this, scrollPane, "📜 Mini Statement", JOptionPane.INFORMATION_MESSAGE);
    }
}
