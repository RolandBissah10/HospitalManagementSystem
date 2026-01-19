package org.example.controller;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import org.example.service.HospitalService;
import org.example.service.PatientService;
import org.example.util.AlertUtils;
import org.example.view.FeedbackView;

import java.sql.SQLException;

public class FeedbackController {
    private final HospitalService hospitalService = new HospitalService();
    private final PatientService patientService = new PatientService();
    private final FeedbackView feedbackView = new FeedbackView();

    public void viewPatientFeedback() {
        try {
            feedbackView.show(hospitalService.getAllFeedback());
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void showFeedbackDialog() {
        feedbackView.showFeedbackDialog(patientService).ifPresent(f -> {
            try {
                hospitalService.addFeedback(f);
                AlertUtils.showAlert("Success", "Feedback Sent", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
