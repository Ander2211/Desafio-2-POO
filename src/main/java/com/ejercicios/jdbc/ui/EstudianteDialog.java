package com.ejercicios.jdbc.ui;

import javax.swing.*;
import java.awt.*;
import com.ejercicios.jdbc.util.Validator;
import com.ejercicios.jdbc.Conexion;
import java.sql.*;


public class EstudianteDialog extends JDialog {
    private JTextField tfNombre = new JTextField(30);
    private JTextField tfEdad = new JTextField(3);
    private JTextField tfDireccion = new JTextField(30);
    private JTextField tfTelefono = new JTextField(10);
    private JComboBox<String> cbGrado; // Nuevo campo para seleccionar grado

    public EstudianteDialog(Frame owner, Integer id) {
        super(owner, true);
        setTitle(id == null ? "Nuevo Estudiante" : "Editar Estudiante");
        setLayout(new GridLayout(7, 2));

        add(new JLabel("Nombre completo:")); add(tfNombre);
        add(new JLabel("Edad:")); add(tfEdad);
        add(new JLabel("Dirección:")); add(tfDireccion);
        add(new JLabel("Teléfono:")); add(tfTelefono);

        add(new JLabel("Grado:"));
        cbGrado = new JComboBox<>(new String[]{"1ero", "2do", "3ero"});
        add(cbGrado);

        JButton btnSave = new JButton("Guardar"); add(btnSave);
        JButton btnCancel = new JButton("Cancelar"); add(btnCancel);

        btnSave.addActionListener(e -> save(id));
        btnCancel.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);

        // Si es edición, cargar datos
        if (id != null) {
            loadData(id);
        }
    }

    private void loadData(Integer id) {
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT e.nombre_completo, e.edad, e.direccion, e.telefono, n.grado " +
                             "FROM estudiante e LEFT JOIN nota n ON e.id_estudiante = n.id_estudiante " +
                             "WHERE e.id_estudiante=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfNombre.setText(rs.getString("nombre_completo"));
                tfEdad.setText(String.valueOf(rs.getInt("edad")));
                tfDireccion.setText(rs.getString("direccion"));
                tfTelefono.setText(rs.getString("telefono"));
                String grado = rs.getString("grado");
                if (grado != null) cbGrado.setSelectedItem(grado);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + ex.getMessage());
        }
    }

    private void save(Integer id) {
        String nombre = tfNombre.getText();
        String edad = tfEdad.getText();
        String direccion = tfDireccion.getText();
        String telefono = tfTelefono.getText();
        String grado = (String) cbGrado.getSelectedItem();

        // Validaciones
        if (!Validator.isNotEmpty(nombre) || !Validator.isOnlyLettersAndSpaces(nombre)) {
            JOptionPane.showMessageDialog(this, "Nombre inválido.");
            return;
        }
        if (!Validator.isValidEdad(edad)) {
            JOptionPane.showMessageDialog(this, "Edad inválida (5-25).");
            return;
        }
        if (!Validator.isNotEmpty(direccion) || direccion.length() < 5) {
            JOptionPane.showMessageDialog(this, "Dirección inválida.");
            return;
        }
        if (!Validator.isValidTelefono(telefono)) {
            JOptionPane.showMessageDialog(this, "Teléfono inválido (8 dígitos).");
            return;
        }

        try (Connection c = Conexion.getConnection()) {
            if (id == null) {
                // Crear nuevo estudiante
                int idEstudiante = -1;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO estudiante(nombre_completo, edad, direccion, telefono) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nombre);
                    ps.setInt(2, Integer.parseInt(edad));
                    ps.setString(3, direccion);
                    ps.setString(4, telefono);
                    ps.executeUpdate();

                    ResultSet keys = ps.getGeneratedKeys();
                    if (keys.next()) {
                        idEstudiante = keys.getInt(1);
                    }
                }

                // Asignar grado en la tabla nota (sin materia aún)
                if (idEstudiante != -1) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO nota(id_estudiante, grado, materia, nota_final) VALUES (?, ?, ?, ?)")) {
                        ps.setInt(1, idEstudiante);
                        ps.setString(2, grado);
                        ps.setString(3, "Pendiente"); // materia placeholder
                        ps.setDouble(4, 0.0); // nota por defecto
                        ps.executeUpdate();
                    }
                }

                JOptionPane.showMessageDialog(this, "Estudiante registrado exitosamente.");
                dispose();

            } else {
                // Actualizar estudiante
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE estudiante SET nombre_completo=?, edad=?, direccion=?, telefono=? WHERE id_estudiante=?")) {
                    ps.setString(1, nombre);
                    ps.setInt(2, Integer.parseInt(edad));
                    ps.setString(3, direccion);
                    ps.setString(4, telefono);
                    ps.setInt(5, id);
                    ps.executeUpdate();
                }

                // Actualizar grado en la tabla nota
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE nota SET grado=? WHERE id_estudiante=?")) {
                    ps.setString(1, grado);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Estudiante actualizado correctamente.");
                dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}