package org.example.model;

public class PrescriptionItem {
    private int id;
    private int prescriptionId;
    private String medication;
    private String dosage;
    private String frequency; // e.g., "3 times daily"
    private int durationDays;
    private String instructions;

    public PrescriptionItem() {}

    public PrescriptionItem(int id, int prescriptionId, String medication, String dosage) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.medication = medication;
        this.dosage = dosage;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(int prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getMedication() { return medication; }
    public void setMedication(String medication) { this.medication = medication; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    @Override
    public String toString() {
        return medication + " - " + dosage + " (" + frequency + ")";
    }
}