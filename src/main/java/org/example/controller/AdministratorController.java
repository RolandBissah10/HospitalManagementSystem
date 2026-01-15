package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.service.HospitalService;
import java.util.Map;

public class AdministratorController {

    // Logic Controllers
    private final PatientController patientController = new PatientController();
    private final DoctorController doctorController = new DoctorController();
    private final AppointmentController appointmentController = new AppointmentController();
    private final InventoryController inventoryController = new InventoryController();
    private final PrescriptionController prescriptionController = new PrescriptionController();
    private final DepartmentController departmentController = new DepartmentController();
    private final FeedbackController feedbackController = new FeedbackController();
    private final ReportController reportController = new ReportController();

    private final HospitalService hospitalService = new HospitalService();

    @FXML
    private Label patientCountLabel;
    @FXML
    private Label doctorCountLabel;
    @FXML
    private Label appointmentCountLabel;
    @FXML
    private Label inventoryCountLabel;
    @FXML
    private VBox contentArea;

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    private void refreshDashboard() {
        try {
            Map<String, Object> stats = hospitalService.getSystemStatistics();
            if (patientCountLabel != null)
                patientCountLabel.setText(String.valueOf(stats.get("totalPatients")));
            if (doctorCountLabel != null)
                doctorCountLabel.setText(String.valueOf(stats.get("totalDoctors")));
            if (appointmentCountLabel != null)
                appointmentCountLabel.setText(String.valueOf(stats.get("totalAppointments")));
            if (inventoryCountLabel != null)
                inventoryCountLabel.setText(String.valueOf(stats.get("totalInventory")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Patient Actions
    @FXML
    private void addPatient() {
        patientController.addPatient();
        refreshDashboard();
    }

    @FXML
    private void updatePatient() {
        patientController.updatePatient();
        refreshDashboard();
    }

    @FXML
    private void deletePatient() {
        patientController.deletePatient();
        refreshDashboard();
    }

    @FXML
    private void viewPatients() {
        patientController.viewPatients();
    }

    @FXML
    private void searchPatients() {
        patientController.searchPatients();
    }

    // Doctor Actions
    @FXML
    private void addDoctor() {
        doctorController.addDoctor();
        refreshDashboard();
    }

    @FXML
    private void updateDoctor() {
        doctorController.updateDoctor();
        refreshDashboard();
    }

    @FXML
    private void deleteDoctor() {
        doctorController.deleteDoctor();
        refreshDashboard();
    }

    @FXML
    private void viewDoctors() {
        doctorController.viewDoctors();
    }

    @FXML
    private void searchDoctors() {
        doctorController.searchDoctors();
    }

    // Appointment Actions
    @FXML
    private void scheduleAppointment() {
        appointmentController.scheduleAppointment();
        refreshDashboard();
    }

    @FXML
    private void updateAppointment() {
        appointmentController.updateAppointment();
        refreshDashboard();
    }

    @FXML
    private void deleteAppointment() {
        appointmentController.deleteAppointment();
        refreshDashboard();
    }

    @FXML
    private void viewAppointments() {
        appointmentController.viewAppointments();
    }

    @FXML
    private void searchAppointments() {
        appointmentController.searchAppointments();
    }

    // Inventory Actions
    @FXML
    private void addInventoryItem() {
        inventoryController.addInventoryItem();
        refreshDashboard();
    }

    @FXML
    private void updateInventoryItem() {
        inventoryController.updateInventoryItem();
        refreshDashboard();
    }

    @FXML
    private void deleteInventoryItem() {
        inventoryController.deleteInventoryItem();
        refreshDashboard();
    }

    @FXML
    private void viewInventory() {
        inventoryController.viewInventory();
    }

    // Prescription Actions
    @FXML
    private void addPrescription() {
        prescriptionController.addPrescription();
        refreshDashboard();
    }

    @FXML
    private void updatePrescription() {
        prescriptionController.updatePrescription();
        refreshDashboard();
    }

    @FXML
    private void deletePrescription() {
        prescriptionController.deletePrescription();
        refreshDashboard();
    }

    @FXML
    private void viewPrescriptions() {
        prescriptionController.viewPrescriptions();
    }

    // System Actions
    @FXML
    private void viewDepartments() {
        departmentController.viewDepartments();
    }

    @FXML
    private void viewPatientFeedback() {
        feedbackController.viewPatientFeedback();
    }

    @FXML
    private void generatePerformanceReport() {
        reportController.generatePerformanceReport();
    }
}
