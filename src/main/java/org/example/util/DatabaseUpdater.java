package org.example.util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseUpdater {

    public static void updateSchema() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Add diagnosis column if it doesn't exist
            try {
                stmt.execute("SELECT diagnosis FROM prescriptions LIMIT 1");
                System.out.println("diagnosis column already exists");
            } catch (SQLException e) {
                stmt.execute("ALTER TABLE prescriptions ADD COLUMN diagnosis TEXT");
                System.out.println("Added diagnosis column to prescriptions table");
            }

            // Add notes column if it doesn't exist
            try {
                stmt.execute("SELECT notes FROM prescriptions LIMIT 1");
                System.out.println("notes column already exists");
            } catch (SQLException e) {
                stmt.execute("ALTER TABLE prescriptions ADD COLUMN notes TEXT");
                System.out.println("Added notes column to prescriptions table");
            }

            // Set default values for existing rows
            stmt.execute("UPDATE prescriptions SET diagnosis = '' WHERE diagnosis IS NULL");
            stmt.execute("UPDATE prescriptions SET notes = '' WHERE notes IS NULL");

            System.out.println("Database schema updated successfully!");
        }
    }
}