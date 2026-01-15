package org.example.controller;

import javafx.fxml.FXML;
import org.example.util.AlertUtils;
import javafx.scene.control.Alert;

public class PatientPortalController {

    private final AppointmentController appointmentController = new AppointmentController();
    private final FeedbackController feedbackController = new FeedbackController();
    private final ReportController reportController = new ReportController();

    @FXML
    private void showPatientAppointments() {
        appointmentController.viewAppointments();
    } // Permission logic simplified

    @FXML
    private void showFeedbackDialog() {
        feedbackController.showFeedbackDialog();
    }

    @FXML
    private void showAbout() {
        reportController.showAbout();
    }
}
