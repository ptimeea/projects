//package org.example;

import org.example.presentation.StoreGUI;

import javax.swing.*;

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Order Management Sistem");
        StoreGUI page = new StoreGUI();
        frame.setContentPane(page.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(850, 550);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    });
}