package org.example;

import javax.swing.*;

public class ATMSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ATM_GUI atm = new ATM_GUI();
            atm.setVisible(true);
        });
    }
}
