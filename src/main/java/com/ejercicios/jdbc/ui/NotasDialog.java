package com.ejercicios.jdbc.ui;

import javax.swing.*;
import java.awt.*;
import com.ejercicios.jdbc.util.Validator;
import com.ejercicios.jdbc.Conexion;
import java.sql.*;

public class NotasDialog extends JDialog {
    private JComboBox<String> cbEstudiantes;
    private JComboBox<String> cbGrado;
    private JComboBox<String> cbMateria;
    private JTextField tfNota = new JTextField(4);

    public NotasDialog(Frame owner) {
        super(owner, true);
        setTitle("Registrar Nota");
        setLayout(new GridLayout(5, 2));

        cbEstudiantes = new JComboBox<>();
        cbGrado = new JComboBox<>(new String[]{"1ero", "2do", "3ero"});
        cbGrado.setEnabled(false);
        cbMateria = new JComboBox<>(new String[]{"Matemáticas", "Ciencias", "Lengua"});

        add(new JLabel("Estudiante:")); add(cbEstudiantes);
        add(new JLabel("Grado:")); add(cbGrado);
        add(new JLabel("Materia:")); add(cbMateria);
        add(new JLabel("Nota final:")); add(tfNota);

        JButton btnSave = new JButton("Guardar");
        add(btnSave); add(new JLabel());

        // Acción del botón
        btnSave.addActionListener(e -> save());

        pack();
        setLocationRelativeTo(owner);

        loadEstudiantes();
    }

    private void loadEstudiantes() {
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id_estudiante, nombre_completo FROM estudiante")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbEstudiantes.addItem(rs.getInt(1) + ": " + rs.getString(2));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando estudiantes: " + ex.getMessage());
        }
    }

    private void save() {
        if (!Validator.isValidNota(tfNota.getText())) {
            JOptionPane.showMessageDialog(this, "Nota inválida (0-10).");
            return;
        }

        String estudianteSel = (String) cbEstudiantes.getSelectedItem();
        if (estudianteSel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione estudiante.");
            return;
        }

        int idEst = Integer.parseInt(estudianteSel.split(":")[0].trim());
        String grado = (String) cbGrado.getSelectedItem();
        String materia = (String) cbMateria.getSelectedItem();
        double nota = Double.parseDouble(tfNota.getText());

        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO nota(id_estudiante, grado, materia, nota_final) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, idEst);
            ps.setString(2, grado);
            ps.setString(3, materia);
            ps.setDouble(4, nota);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Nota registrada correctamente.");
            dispose();
        } catch (SQLIntegrityConstraintViolationException dup) {
            JOptionPane.showMessageDialog(this, "Ya existe una nota para ese estudiante, grado y materia.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }
}