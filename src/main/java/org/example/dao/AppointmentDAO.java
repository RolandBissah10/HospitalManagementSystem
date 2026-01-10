package org.example.dao;

import org.example.model.Appointment;
import org.example.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private long totalQueryTime = 0;
    private int queryCount = 0;

    public void addAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?)";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(5, appointment.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } finally {
            updatePerformanceStats(startTime);
        }
    }

    public Appointment getAppointment(int id) throws SQLException {
        String sql = "SELECT a.*, p.first_name as p_first, p.last_name as p_last, d.first_name as d_first, d.last_name as d_last "
                +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.id " +
                "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE a.id = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("status"));
                String pFirst = rs.getString("p_first");
                String pLast = rs.getString("p_last");
                if (pFirst != null && pLast != null) {
                    appt.setPatientName(pFirst + " " + pLast);
                } else {
                    appt.setPatientName("Unknown");
                }

                String dFirst = rs.getString("d_first");
                String dLast = rs.getString("d_last");
                if (dFirst != null && dLast != null) {
                    appt.setDoctorName("Dr. " + dFirst + " " + dLast);
                } else {
                    appt.setDoctorName("Unknown");
                }
                return appt;
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return null;
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name as p_first, p.last_name as p_last, d.first_name as d_first, d.last_name as d_last "
                +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.id " +
                "LEFT JOIN doctors d ON a.doctor_id = d.id";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("status"));
                String pFirst = rs.getString("p_first");
                String pLast = rs.getString("p_last");
                if (pFirst != null && pLast != null) {
                    appt.setPatientName(pFirst + " " + pLast);
                } else {
                    appt.setPatientName("Unknown");
                }

                String dFirst = rs.getString("d_first");
                String dLast = rs.getString("d_last");
                if (dFirst != null && dLast != null) {
                    appt.setDoctorName("Dr. " + dFirst + " " + dLast);
                } else {
                    appt.setDoctorName("Unknown");
                }
                appointments.add(appt);
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return appointments;
    }

    public void updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, appointment_date = ?, appointment_time = ?, status = ? WHERE id = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(5, appointment.getStatus());
            stmt.setInt(6, appointment.getId());
            stmt.executeUpdate();

        } finally {
            updatePerformanceStats(startTime);
        }
    }

    public void deleteAppointment(int id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } finally {
            updatePerformanceStats(startTime);
        }
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name as p_first, p.last_name as p_last, d.first_name as d_first, d.last_name as d_last "
                +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.id " +
                "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE a.appointment_date BETWEEN ? AND ? ORDER BY a.appointment_date, a.appointment_time";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appt = new Appointment(
                            rs.getInt("id"),
                            rs.getInt("patient_id"),
                            rs.getInt("doctor_id"),
                            rs.getDate("appointment_date").toLocalDate(),
                            rs.getTime("appointment_time").toLocalTime(),
                            rs.getString("status"));
                    String pFirst = rs.getString("p_first");
                    String pLast = rs.getString("p_last");
                    if (pFirst != null && pLast != null) {
                        appt.setPatientName(pFirst + " " + pLast);
                    } else {
                        appt.setPatientName("Unknown");
                    }

                    String dFirst = rs.getString("d_first");
                    String dLast = rs.getString("d_last");
                    if (dFirst != null && dLast != null) {
                        appt.setDoctorName("Dr. " + dFirst + " " + dLast);
                    } else {
                        appt.setDoctorName("Unknown");
                    }
                    appointments.add(appt);
                }
            }

        } finally {
            updatePerformanceStats(startTime);
        }
        return appointments;
    }

    public int getAppointmentCountByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM appointments WHERE status = ?";
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
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