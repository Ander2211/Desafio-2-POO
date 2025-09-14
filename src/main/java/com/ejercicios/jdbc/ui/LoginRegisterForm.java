package com.ejercicios.jdbc.ui;

import javax.swing.*;
import java.awt.*;
import com.ejercicios.jdbc.Conexion;
import com.ejercicios.jdbc.util.Validator;
import java.sql.*;


public class LoginRegisterForm extends JFrame {
    private JTextField tfUser = new JTextField(20);
    private JPasswordField pfPass = new JPasswordField(20);

    public LoginRegisterForm() {
        super("Login - Colegio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(3, 2));
        p.add(new JLabel("Usuario:")); p.add(tfUser);
        p.add(new JLabel("Contraseña:")); p.add(pfPass);

        JButton btnLogin = new JButton("Login");
        JButton btnReg = new JButton("Registrar");
        JPanel p2 = new JPanel(); p2.add(btnLogin); p2.add(btnReg);

        add(p, BorderLayout.CENTER);
        add(p2, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);

        btnLogin.addActionListener(e -> login());
        btnReg.addActionListener(e -> doRegister());
    }

    private void login() {
        String user = tfUser.getText();
        String pass = new String(pfPass.getPassword());

        if (!Validator.isNotEmpty(user) || !Validator.isNotEmpty(pass)) {
            JOptionPane.showMessageDialog(this, "Usuario y contraseña no pueden estar vacíos.");
            return;
        }

        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT password_hash FROM usuario WHERE username=?")) {
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString(1);
                if (hash.equals(pass)) { // ⚠️ En producción usar hash seguro como bcrypt
                    JOptionPane.showMessageDialog(this, "Login exitoso");
                    new MainForm().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrecta");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Usuario no registrado");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error conexión: " + ex.getMessage());
        }
    }

    private void doRegister() {
        String user = tfUser.getText();
        String pass = new String(pfPass.getPassword());

        if (!Validator.isNotEmpty(user) || !Validator.isValidPassword(pass)) {
            JOptionPane.showMessageDialog(this, "Usuario vacío o contraseña menor a 8 caracteres.");
            return;
        }

        try (Connection c = Conexion.getConnection();
             PreparedStatement psCheck = c.prepareStatement("SELECT id_usuario FROM usuario WHERE username=?")) {
            psCheck.setString(1, user);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Usuario ya existe.");
                return;
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO usuario(username, password_hash) VALUES(?, ?)")) {
                ps.setString(1, user);
                ps.setString(2, pass); // ⚠️ En producción se debe guardar un hash, no texto plano
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registro exitoso.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}