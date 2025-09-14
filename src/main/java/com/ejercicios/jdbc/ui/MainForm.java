package com.ejercicios.jdbc.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.ejercicios.jdbc.Conexion;
import java.sql.*;
import java.awt.*;


public class MainForm extends JFrame {
    private JTree treeGrados;
    private DefaultTreeModel treeModel;

    private JTable tableStudents;
    private DefaultTableModel tableModel;

    public MainForm() {
        super("Gestión de Estudiantes y Notas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // ==== Panel de botones ====
        JPanel top = new JPanel();
        JButton btnNewStudent = new JButton("Nuevo Estudiante");
        JButton btnEditStudent = new JButton("Editar Estudiante");
        JButton btnDeleteStudent = new JButton("Eliminar Estudiante");
        JButton btnNewNota = new JButton("Nueva Nota");
        JButton btnRefresh = new JButton("Refrescar");

        top.add(btnNewStudent);
        top.add(btnEditStudent);
        top.add(btnDeleteStudent);
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(btnNewNota);
        top.add(btnRefresh);

        add(top, BorderLayout.NORTH);

        // ==== Panel central dividido ====
        JSplitPane splitPane = new JSplitPane();
        add(splitPane, BorderLayout.CENTER);

        // ==== Árbol de grados ====
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Grados");
        treeModel = new DefaultTreeModel(root);
        treeGrados = new JTree(treeModel);
        splitPane.setLeftComponent(new JScrollPane(treeGrados));

        // ==== Tabla de estudiantes ====
        String[] cols = {"ID", "Nombre", "Edad", "Dirección", "Teléfono", "Grado"};
        tableModel = new DefaultTableModel(cols, 0);
        tableStudents = new JTable(tableModel);
        splitPane.setRightComponent(new JScrollPane(tableStudents));

        // ==== Acciones de los botones ====
        btnNewStudent.addActionListener(e -> {
            new EstudianteDialog(this, null).setVisible(true);
            loadData();
        });

        btnEditStudent.addActionListener(e -> {
            int row = tableStudents.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un estudiante para editar.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            new EstudianteDialog(this, id).setVisible(true);
            loadData();
        });

        btnDeleteStudent.addActionListener(e -> {
            int row = tableStudents.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un estudiante para eliminar.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que desea eliminar este estudiante?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteStudent(id);
                loadData();
            }
        });

        btnNewNota.addActionListener(e -> {
            new NotasDialog(this).setVisible(true);
            loadData();
        });

        btnRefresh.addActionListener(e -> loadData());

        // ==== Cargar datos al inicio ====
        loadData();
    }

    private void loadData() {
        loadTree();
        loadTable();
    }

    private void loadTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Grados");

        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT e.id_estudiante, e.nombre_completo, n.grado " +
                             "FROM estudiante e " +
                             "LEFT JOIN nota n ON e.id_estudiante = n.id_estudiante " +
                             "ORDER BY n.grado, e.nombre_completo")) {

            ResultSet rs = ps.executeQuery();
            String gradoActual = null;
            DefaultMutableTreeNode nodoGrado = null;

            while (rs.next()) {
                String grado = rs.getString("grado");
                if (grado == null) grado = "Sin Grado";

                if (!grado.equals(gradoActual)) {
                    nodoGrado = new DefaultMutableTreeNode(grado);
                    root.add(nodoGrado);
                    gradoActual = grado;
                }

                String estudiante = rs.getInt("id_estudiante") + " - " + rs.getString("nombre_completo");
                if (nodoGrado != null) {
                    nodoGrado.add(new DefaultMutableTreeNode(estudiante));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando árbol: " + ex.getMessage());
        }

        treeModel.setRoot(root);
        treeGrados.setModel(treeModel);
    }

    private void loadTable() {
        tableModel.setRowCount(0); // limpiar
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT e.id_estudiante, e.nombre_completo, e.edad, e.direccion, e.telefono, n.grado " +
                             "FROM estudiante e " +
                             "LEFT JOIN nota n ON e.id_estudiante = n.id_estudiante " +
                             "ORDER BY e.id_estudiante")) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id_estudiante"),
                        rs.getString("nombre_completo"),
                        rs.getInt("edad"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("grado") != null ? rs.getString("grado") : "Sin Grado"
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando tabla: " + ex.getMessage());
        }
    }

    private void deleteStudent(int id) {
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM estudiante WHERE id_estudiante=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error eliminando estudiante: " + ex.getMessage());
        }
    }
}