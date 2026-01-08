package org.example.model;

import java.time.LocalDate;

public class Prescription {
    private int id;
    private int patientId;
    private int doctorId;
    private LocalDate prescriptionDate;
    private String diagnosis;
    private String notes;
    private String patientName; // For display purposes
    private String doctorName;  // For display purposes

    public Prescription() {}

    public Prescription(int id, int patientId, int doctorId, LocalDate prescriptionDate) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.prescriptionDate = prescriptionDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    @Override
    public String toString() {
        return "Prescription #" + id + " for Patient ID " + patientId + " on " + prescriptionDate;
    }
}