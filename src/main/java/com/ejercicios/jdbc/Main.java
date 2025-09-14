package com.ejercicios.jdbc;

import com.ejercicios.jdbc.ui.LoginRegisterForm;

import javax.swing.*;

public class  Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginRegisterForm().setVisible(true);
        });
    }
}