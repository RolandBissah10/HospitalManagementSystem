package org.example.model;

import org.bson.Document;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class MedicalLog {
    private String id; // MongoDB _id
    private int patientId;
    private String logContent;
    private String severity; // Low, Medium, High
    private LocalDateTime timestamp;

    public MedicalLog() {
    }

    public MedicalLog(int patientId, String logContent, String severity, LocalDateTime timestamp) {
        this.patientId = patientId;
        this.logContent = logContent;
        this.severity = severity;
        this.timestamp = timestamp;
    }

    public Document toDocument() {
        return new Document("patient_id", patientId)
                .append("log_content", logContent)
                .append("severity", severity)
                .append("timestamp", Date.from(timestamp.atZone(ZoneId.systemDefault()).toInstant()));
    }

    public static MedicalLog fromDocument(Document doc) {
        MedicalLog log = new MedicalLog();
        if (doc.getObjectId("_id") != null)
            log.setId(doc.getObjectId("_id").toString());
        log.setPatientId(doc.getInteger("patient_id"));
        log.setLogContent(doc.getString("log_content"));
        log.setSeverity(doc.getString("severity"));
        Date date = doc.getDate("timestamp");
        log.setTimestamp(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return log;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
