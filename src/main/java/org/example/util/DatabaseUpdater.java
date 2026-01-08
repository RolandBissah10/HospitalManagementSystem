package org.example.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUpdater {

    public static void updateSchema() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // 1. Add missing columns
            ensureColumnExists(conn, "prescriptions", "diagnosis", "TEXT");
            ensureColumnExists(conn, "prescriptions", "notes", "TEXT");
            ensureColumnExists(conn, "patient_feedback", "appointment_id", "INT");

            // 2. Fix foreign key constraints dynamically
            // 2. Fix foreign key constraints dynamically
            // We need to drop any old constraints that might be blocking operations
            fixForeignKeys(conn, "appointments", "patient_id", "patients", "id", "CASCADE");
            fixForeignKeys(conn, "prescriptions", "patient_id", "patients", "id", "CASCADE");
            fixForeignKeys(conn, "prescription_items", "prescription_id", "prescriptions", "id", "CASCADE");
            fixForeignKeys(conn, "patient_feedback", "patient_id", "patients", "id", "CASCADE");
            fixForeignKeys(conn, "patient_feedback", "appointment_id", "appointments", "id", "SET NULL");
            fixForeignKeys(conn, "doctors", "department_id", "departments", "id", "SET NULL");

            // 3. Set default values
            stmt.execute("UPDATE prescriptions SET diagnosis = '' WHERE diagnosis IS NULL");
            stmt.execute("UPDATE prescriptions SET notes = '' WHERE notes IS NULL");

            System.out.println("Database schema updated successfully!");
        }
    }

    private static void ensureColumnExists(Connection conn, String table, String column, String type)
            throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, table, column)) {
            if (!rs.next()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
                }
            }
        }
    }

    private static void fixForeignKeys(Connection conn, String table, String column, String refTable, String refColumn,
            String onDelete)
            throws SQLException {
        // Query INFORMATION_SCHEMA to find existing constraint names for this column
        String sql = "SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ? " +
                "AND REFERENCED_TABLE_NAME = ?";

        List<String> constraints = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, table);
            pstmt.setString(2, column);
            pstmt.setString(3, refTable);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    constraints.add(rs.getString("CONSTRAINT_NAME"));
                }
            }
        }

        try (Statement stmt = conn.createStatement()) {
            // Drop found constraints
            for (String constraint : constraints) {
                try {
                    stmt.execute("ALTER TABLE " + table + " DROP FOREIGN KEY " + constraint);
                } catch (SQLException ignored) {
                }
            }

            // Add new constraint with specified action
            String fkName = "fk_" + table + "_" + column;
            stmt.execute("ALTER TABLE " + table + " ADD CONSTRAINT " + fkName +
                    " FOREIGN KEY (" + column + ") REFERENCES " + refTable + "(" + refColumn + ") ON DELETE "
                    + onDelete);
        }
    }
}