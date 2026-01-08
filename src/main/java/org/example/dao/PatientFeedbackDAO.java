package org.example.dao;

import org.example.model.PatientFeedback;
import org.example.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientFeedbackDAO {

    public void addFeedback(PatientFeedback feedback) throws SQLException {
        String sql = "INSERT INTO patient_feedback (patient_id, appointment_id, rating, comments, feedback_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, feedback.getPatientId());
            if (feedback.getAppointmentId() != null) {
                stmt.setInt(2, feedback.getAppointmentId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, feedback.getRating());
            stmt.setString(4, feedback.getComments());
            stmt.setTimestamp(5, Timestamp.valueOf(feedback.getFeedbackDate()));

            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next())
                        feedback.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<PatientFeedback> getAllFeedback() throws SQLException {
        List<PatientFeedback> list = new ArrayList<>();
        String sql = "SELECT * FROM patient_feedback ORDER BY feedback_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToFeedback(rs));
            }
        }
        return list;
    }

    public List<PatientFeedback> getFeedbackByPatient(int patientId) throws SQLException {
        List<PatientFeedback> list = new ArrayList<>();
        String sql = "SELECT * FROM patient_feedback WHERE patient_id = ? ORDER BY feedback_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(mapResultSetToFeedback(rs));
            }
        }
        return list;
    }

    private PatientFeedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        PatientFeedback feedback = new PatientFeedback();
        feedback.setId(rs.getInt("id"));
        feedback.setPatientId(rs.getInt("patient_id"));
        int appId = rs.getInt("appointment_id");
        if (!rs.wasNull())
            feedback.setAppointmentId(appId);
        feedback.setRating(rs.getInt("rating"));
        feedback.setComments(rs.getString("comments"));
        feedback.setFeedbackDate(rs.getTimestamp("feedback_date").toLocalDateTime());
        return feedback;
    }
}
