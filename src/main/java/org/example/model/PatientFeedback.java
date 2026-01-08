package org.example.model;

import java.time.LocalDateTime;

public class PatientFeedback {
    private int id;
    private int patientId;
    private Integer appointmentId;
    private int rating;
    private String comments;
    private LocalDateTime feedbackDate;

    public PatientFeedback() {}

    public PatientFeedback(int id, int patientId, Integer appointmentId, int rating,
                           String comments, LocalDateTime feedbackDate) {
        this.id = id;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = feedbackDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public int getRating() { return rating; }
    public void setRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
    }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(LocalDateTime feedbackDate) { this.feedbackDate = feedbackDate; }

    @Override
    public String toString() {
        return "Rating: " + rating + " - " +
                (comments != null && comments.length() > 20 ?
                        comments.substring(0, 20) + "..." : comments);
    }
}