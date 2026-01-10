package org.example.dao;

import org.example.model.Doctor;
import org.example.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    private long totalQueryTime = 0;
    private int queryCount = 0;

    public void addDoctor(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors (first_name, last_name, specialty, department_id, phone, email) VALUES (?, ?, ?, ?, ?, ?)";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, doctor.getFirstName());
            stmt.setString(2, doctor.getLastName());
            stmt.setString(3, doctor.getSpecialty());
            stmt.setInt(4, doctor.getDepartmentId());
            stmt.setString(5, doctor.getPhone());
            stmt.setString(6, doctor.getEmail());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        doctor.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } finally {
            updatePerformanceStats(startTime);
        }
    }

    public Doctor getDoctor(String email) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE email = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Doctor(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("specialty"),
                        rs.getInt("department_id"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return null;
    }

    public Doctor getDoctorById(int id) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE id = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Doctor(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("specialty"),
                        rs.getInt("department_id"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return null;
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                doctors.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("specialty"),
                        rs.getInt("department_id"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return doctors;
    }

    public void updateDoctor(Doctor doctor, String originalEmail) throws SQLException {
        String sql = "UPDATE doctors SET first_name = ?, last_name = ?, specialty = ?, department_id = ?, phone = ?, email = ? WHERE email = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.getFirstName());
            stmt.setString(2, doctor.getLastName());
            stmt.setString(3, doctor.getSpecialty());
            stmt.setInt(4, doctor.getDepartmentId());
            stmt.setString(5, doctor.getPhone());
            stmt.setString(6, doctor.getEmail());
            stmt.setString(7, originalEmail);
            stmt.executeUpdate();

        } finally {
            updatePerformanceStats(startTime);
        }
    }

    public void deleteDoctor(int id) throws SQLException {
        String sql = "DELETE FROM doctors WHERE id = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } finally {
            updatePerformanceStats(startTime);
        }
    }

    public List<Doctor> searchDoctors(String searchTerm) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE first_name LIKE ? OR last_name LIKE ? OR specialty LIKE ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(new Doctor(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("specialty"),
                            rs.getInt("department_id"),
                            rs.getString("phone"),
                            rs.getString("email")
                    ));
                }
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return doctors;
    }

    private void updatePerformanceStats(long startTime) {
        long endTime = System.currentTimeMillis();
        totalQueryTime += (endTime - startTime);
        queryCount++;
    }

    public double getAverageQueryTime() {
        return queryCount > 0 ? (double) totalQueryTime / queryCount : 0;
    }
}