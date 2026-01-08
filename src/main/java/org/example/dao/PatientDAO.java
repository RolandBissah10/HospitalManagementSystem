package org.example.dao;

import org.example.model.Patient;
import org.example.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    private long totalQueryTime = 0;
    private int queryCount = 0;

    public void addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (first_name, last_name, date_of_birth, address, phone, email) VALUES (?, ?, ?, ?, ?, ?)";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(4, patient.getAddress());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getEmail());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } finally {
            updatePerformanceStats(startTime);
        }
    }

    public Patient getPatient(int id) throws SQLException {
        String sql = "SELECT * FROM patients WHERE id = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Patient(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth").toLocalDate(),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"));
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return null;
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth").toLocalDate(),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")));
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return patients;
    }

    public void updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, date_of_birth = ?, address = ?, phone = ?, email = ? WHERE id = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(4, patient.getAddress());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getEmail());
            stmt.setInt(7, patient.getId());
            stmt.executeUpdate();

        } finally {
            updatePerformanceStats(startTime);
        }
    }

    public void deletePatient(int id) throws SQLException {
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Manual deletion of child records to ensure success even if CASCADE fails
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM prescription_items WHERE prescription_id IN (SELECT id FROM prescriptions WHERE patient_id = ?)")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM prescriptions WHERE patient_id = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM appointments WHERE patient_id = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn
                        .prepareStatement("DELETE FROM patient_feedback WHERE patient_id = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM patients WHERE id = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } finally {
            updatePerformanceStats(startTime);
        }
    }

    public List<Patient> searchPatients(String name) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE CONCAT(first_name, ' ', last_name) LIKE ? OR first_name LIKE ? OR last_name LIKE ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + name + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth").toLocalDate(),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")));
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return patients;
    }

    public int getTotalPatientCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM patients";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return 0;
    }

    private void updatePerformanceStats(long startTime) {
        long endTime = System.currentTimeMillis();
        totalQueryTime += (endTime - startTime);
        queryCount++;
    }

    public double getAverageQueryTime() {
        return queryCount > 0 ? (double) totalQueryTime / queryCount : 0;
    }

    public void resetPerformanceStats() {
        totalQueryTime = 0;
        queryCount = 0;
    }
}