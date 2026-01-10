package org.example.dao;

import org.example.model.Prescription;
import org.example.model.PrescriptionItem;
import org.example.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {

    // Add prescription with items in a transaction
    public void addPrescriptionWithItems(Prescription prescription, List<PrescriptionItem> items) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String prescriptionSQL = "INSERT INTO prescriptions (patient_id, doctor_id, prescription_date, diagnosis, notes) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement prescriptionStmt = conn.prepareStatement(prescriptionSQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                prescriptionStmt.setInt(1, prescription.getPatientId());
                prescriptionStmt.setInt(2, prescription.getDoctorId());
                prescriptionStmt.setDate(3, Date.valueOf(prescription.getPrescriptionDate()));
                prescriptionStmt.setString(4, prescription.getDiagnosis());
                prescriptionStmt.setString(5, prescription.getNotes());

                int affectedRows = prescriptionStmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = prescriptionStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int prescriptionId = generatedKeys.getInt(1);
                            prescription.setId(prescriptionId);

                            String itemSQL = "INSERT INTO prescription_items (prescription_id, medication, dosage) VALUES (?, ?, ?)";
                            try (PreparedStatement itemStmt = conn.prepareStatement(itemSQL)) {
                                for (PrescriptionItem item : items) {
                                    itemStmt.setInt(1, prescriptionId);
                                    itemStmt.setString(2, item.getMedication());
                                    itemStmt.setString(3, item.getDosage());
                                    itemStmt.addBatch();
                                }
                                itemStmt.executeBatch();
                            }
                            conn.commit();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void updatePrescriptionWithItems(Prescription prescription, List<PrescriptionItem> items)
            throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Update prescription
            String prescriptionSQL = "UPDATE prescriptions SET patient_id = ?, doctor_id = ?, prescription_date = ?, diagnosis = ?, notes = ? WHERE id = ?";
            try (PreparedStatement prescriptionStmt = conn.prepareStatement(prescriptionSQL)) {
                prescriptionStmt.setInt(1, prescription.getPatientId());
                prescriptionStmt.setInt(2, prescription.getDoctorId());
                prescriptionStmt.setDate(3, Date.valueOf(prescription.getPrescriptionDate()));
                prescriptionStmt.setString(4, prescription.getDiagnosis());
                prescriptionStmt.setString(5, prescription.getNotes());
                prescriptionStmt.setInt(6, prescription.getId());
                prescriptionStmt.executeUpdate();
            }

            // Delete old items
            String deleteItemsSQL = "DELETE FROM prescription_items WHERE prescription_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteItemsSQL)) {
                deleteStmt.setInt(1, prescription.getId());
                deleteStmt.executeUpdate();
            }

            // Insert new items
            String itemSQL = "INSERT INTO prescription_items (prescription_id, medication, dosage) VALUES (?, ?, ?)";
            try (PreparedStatement itemStmt = conn.prepareStatement(itemSQL)) {
                for (PrescriptionItem item : items) {
                    itemStmt.setInt(1, prescription.getId());
                    itemStmt.setString(2, item.getMedication());
                    itemStmt.setString(3, item.getDosage());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void addPrescription(Prescription prescription) throws SQLException {
        String sql = "INSERT INTO prescriptions (patient_id, doctor_id, prescription_date, diagnosis, notes) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, prescription.getPatientId());
            stmt.setInt(2, prescription.getDoctorId());
            stmt.setDate(3, Date.valueOf(prescription.getPrescriptionDate()));

            // Handle optional columns
            if (prescription.getDiagnosis() != null) {
                stmt.setString(4, prescription.getDiagnosis());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }

            if (prescription.getNotes() != null) {
                stmt.setString(5, prescription.getNotes());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        prescription.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    public void addPrescriptionItem(PrescriptionItem item) throws SQLException {
        String sql = "INSERT INTO prescription_items (prescription_id, medication, dosage) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getPrescriptionId());
            stmt.setString(2, item.getMedication());
            stmt.setString(3, item.getDosage());
            stmt.executeUpdate();
        }
    }

    public Prescription getPrescription(int id) throws SQLException {
        String sql = "SELECT p.*, " +
                "pat.first_name as patient_first_name, pat.last_name as patient_last_name, " +
                "doc.first_name as doctor_first_name, doc.last_name as doctor_last_name " +
                "FROM prescriptions p " +
                "LEFT JOIN patients pat ON p.patient_id = pat.id " +
                "LEFT JOIN doctors doc ON p.doctor_id = doc.id " +
                "WHERE p.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Prescription prescription = new Prescription(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("prescription_date").toLocalDate());

                // Safely get optional columns
                String diagnosis = rs.getString("diagnosis");
                if (diagnosis != null) {
                    prescription.setDiagnosis(diagnosis);
                } else {
                    prescription.setDiagnosis("");
                }

                String notes = rs.getString("notes");
                if (notes != null) {
                    prescription.setNotes(notes);
                } else {
                    prescription.setNotes("");
                }

                prescription
                        .setPatientName(rs.getString("patient_first_name") + " " + rs.getString("patient_last_name"));
                prescription.setDoctorName(
                        "Dr. " + rs.getString("doctor_first_name") + " " + rs.getString("doctor_last_name"));
                return prescription;
            }
        }
        return null;
    }

    public List<Prescription> getPrescriptionsByPatient(int patientId) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, doc.first_name as doctor_first_name, doc.last_name as doctor_last_name " +
                "FROM prescriptions p " +
                "LEFT JOIN doctors doc ON p.doctor_id = doc.id " +
                "WHERE p.patient_id = ? ORDER BY p.prescription_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Prescription prescription = new Prescription(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("prescription_date").toLocalDate());

                String diagnosis = rs.getString("diagnosis");
                if (diagnosis != null) {
                    prescription.setDiagnosis(diagnosis);
                }

                String notes = rs.getString("notes");
                if (notes != null) {
                    prescription.setNotes(notes);
                }

                prescription.setDoctorName(
                        "Dr. " + rs.getString("doctor_first_name") + " " + rs.getString("doctor_last_name"));
                prescriptions.add(prescription);
            }
        }
        return prescriptions;
    }

    public List<Prescription> getPrescriptionsByDoctor(int doctorId) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, pat.first_name as patient_first_name, pat.last_name as patient_last_name " +
                "FROM prescriptions p " +
                "LEFT JOIN patients pat ON p.patient_id = pat.id " +
                "WHERE p.doctor_id = ? ORDER BY p.prescription_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Prescription prescription = new Prescription(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("prescription_date").toLocalDate());

                String diagnosis = rs.getString("diagnosis");
                if (diagnosis != null) {
                    prescription.setDiagnosis(diagnosis);
                }

                String notes = rs.getString("notes");
                if (notes != null) {
                    prescription.setNotes(notes);
                }

                prescription.setPatientName(
                        rs.getString("patient_first_name") + " " + rs.getString("patient_last_name"));
                prescriptions.add(prescription);
            }
        }
        return prescriptions;
    }

    public List<Prescription> getAllPrescriptions() throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, " +
                "pat.first_name as patient_first_name, pat.last_name as patient_last_name, " +
                "doc.first_name as doctor_first_name, doc.last_name as doctor_last_name " +
                "FROM prescriptions p " +
                "LEFT JOIN patients pat ON p.patient_id = pat.id " +
                "LEFT JOIN doctors doc ON p.doctor_id = doc.id " +
                "ORDER BY p.prescription_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Prescription prescription = new Prescription(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("prescription_date").toLocalDate());

                String diagnosis = rs.getString("diagnosis");
                if (diagnosis != null) {
                    prescription.setDiagnosis(diagnosis);
                }

                String notes = rs.getString("notes");
                if (notes != null) {
                    prescription.setNotes(notes);
                }

                prescription
                        .setPatientName(rs.getString("patient_first_name") + " " + rs.getString("patient_last_name"));
                prescription.setDoctorName(
                        "Dr. " + rs.getString("doctor_first_name") + " " + rs.getString("doctor_last_name"));
                prescriptions.add(prescription);
            }
        }
        return prescriptions;
    }

    public List<PrescriptionItem> getPrescriptionItems(int prescriptionId) throws SQLException {
        List<PrescriptionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM prescription_items WHERE prescription_id = ? ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, prescriptionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PrescriptionItem item = new PrescriptionItem(
                        rs.getInt("id"),
                        rs.getInt("prescription_id"),
                        rs.getString("medication"),
                        rs.getString("dosage"));
                items.add(item);
            }
        }
        return items;
    }

    public void deletePrescription(int id) throws SQLException {
        String sql = "DELETE FROM prescriptions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Prescription> searchPrescriptions(String searchTerm) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, " +
                "pat.first_name as patient_first_name, pat.last_name as patient_last_name, " +
                "doc.first_name as doctor_first_name, doc.last_name as doctor_last_name " +
                "FROM prescriptions p " +
                "LEFT JOIN patients pat ON p.patient_id = pat.id " +
                "LEFT JOIN doctors doc ON p.doctor_id = doc.id " +
                "WHERE pat.first_name LIKE ? OR pat.last_name LIKE ? OR " +
                "doc.first_name LIKE ? OR doc.last_name LIKE ? OR " +
                "COALESCE(p.diagnosis, '') LIKE ? " +
                "ORDER BY p.prescription_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            stmt.setString(4, likeTerm);
            stmt.setString(5, likeTerm);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Prescription prescription = new Prescription(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("prescription_date").toLocalDate());

                String diagnosis = rs.getString("diagnosis");
                if (diagnosis != null) {
                    prescription.setDiagnosis(diagnosis);
                }

                String notes = rs.getString("notes");
                if (notes != null) {
                    prescription.setNotes(notes);
                }

                prescription
                        .setPatientName(rs.getString("patient_first_name") + " " + rs.getString("patient_last_name"));
                prescription.setDoctorName(
                        "Dr. " + rs.getString("doctor_first_name") + " " + rs.getString("doctor_last_name"));
                prescriptions.add(prescription);
            }
        }
        return prescriptions;
    }
}