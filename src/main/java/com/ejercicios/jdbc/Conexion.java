package com.ejercicios.jdbc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_escolar";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static Connection conn;

    public static Connection getConnection() throws Exception {
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                throw new SQLException("Error al conectar con la base de datos: " + e.getMessage(), e);
            }
        }
        return conn;
    }

    public static void closeQuietly(AutoCloseable r) {
        if (r == null) return;
        try { r.close(); } catch (Exception ignore) {}
    }
}