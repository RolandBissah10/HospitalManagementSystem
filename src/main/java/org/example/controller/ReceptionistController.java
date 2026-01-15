package org.example.controller;

import javafx.fxml.FXML;

public class ReceptionistController {

    private final PatientController patientController = new PatientController();
    private final AppointmentController appointmentController = new AppointmentController();
    private final DoctorController doctorController = new DoctorController();

    @FXML
    private void addPatient() {
        patientController.addPatient();
    }

    @FXML
    private void scheduleAppointment() {
        appointmentController.scheduleAppointment();
    }

    @FXML
    private void viewDoctors() {
        doctorController.viewDoctors();
    }

    @FXML
    private void viewPatients() {
        patientController.viewPatients();
    }

    @FXML
    private void searchPatients() {
        patientController.searchPatients();
    }
}
