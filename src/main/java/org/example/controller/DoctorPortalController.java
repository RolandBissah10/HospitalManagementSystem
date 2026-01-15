package org.example.controller;

import javafx.fxml.FXML;

public class DoctorPortalController {

    private final AppointmentController appointmentController = new AppointmentController();
    private final PatientController patientController = new PatientController();
    private final PrescriptionController prescriptionController = new PrescriptionController();
    private final DoctorController doctorController = new DoctorController();
    private final MedicalLogController medicalLogController = new MedicalLogController();

    @FXML
    private void viewAppointments() {
        appointmentController.viewAppointments();
    }

    @FXML
    private void cancelAppointment() {
        appointmentController.deleteAppointment();
    } // Logic mapped

    @FXML
    private void viewPatients() {
        patientController.viewPatients();
    }

    @FXML
    private void searchPatients() {
        patientController.searchPatients();
    }

    @FXML
    private void viewDoctorsForDoctor() {
        doctorController.viewDoctors();
    }

    @FXML
    private void addPrescription() {
        prescriptionController.addPrescription();
    }

    @FXML
    private void viewDoctorPrescriptions() {
        prescriptionController.viewPrescriptions();
    } // Simplified mapping

    // NoSQL Features
    @FXML
    private void addMedicalLog() {
        medicalLogController.addMedicalLog();
    }

    @FXML
    private void viewMedicalLogs() {
        medicalLogController.viewMedicalLogs();
    }
}
