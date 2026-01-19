package org.example.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import org.example.dao.MedicalLogDAO;
import org.example.model.Patient;
import org.example.service.PatientService;
import org.example.service.PrescriptionService;
import org.example.util.AlertUtils;
import org.example.view.MedicalHistoryView;
import org.example.view.PatientFormView;
import org.example.view.PatientTableView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientController {

    private final PatientService patientService = new PatientService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final MedicalLogDAO medicalLogDAO = new MedicalLogDAO();

    // View Components
    private final PatientFormView patientFormView = new PatientFormView();
    private final PatientTableView patientTableView = new PatientTableView();
    private final MedicalHistoryView medicalHistoryView = new MedicalHistoryView();

    // The methods below are copied and adapted from MainController

    public void addPatient() {
        Optional<Patient> result = patientFormView.showAddPatientDialog(patientService);
        result.ifPresent(patient -> {
            try {
                patientService.addPatient(patient);
                AlertUtils.showAlert("Success", "Patient added successfully (ID: " + patient.getId() + ")",
                        Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Database Error", "Failed to add patient: " + e.getMessage(),
                        Alert.AlertType.ERROR);
            }
        });
    }

    public void viewPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            patientTableView.show("All Patients", patients, this::showMedicalHistory);
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to load patients: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void updatePatient() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Update Patient");
            dialog.setHeaderText("Enter Patient email to update:");
            dialog.setContentText("Patient Email:");

            Optional<String> emailResult = dialog.showAndWait();
            if (emailResult.isPresent() && !emailResult.get().isEmpty()) {
                String patientEmail = emailResult.get().trim();

                Patient patient = patientService.getPatient(patientEmail);
                if (patient == null) {
                    AlertUtils.showAlert("Not Found", "Patient with email " + patientEmail + " not found.",
                            Alert.AlertType.ERROR);
                    return;
                }

                patientFormView.showUpdatePatientDialog(patient, patientService, patientEmail, null);
            }
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to update patient: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            AlertUtils.showAlert("Input Error", "Please enter a valid email address: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    public void deletePatient() {
        try {
            Optional<Integer> idResult = patientFormView.askForPatientId("Delete Patient");
            if (idResult.isPresent()) {
                int patientId = idResult.get();
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText("Delete Patient ID: " + patientId);
                confirmAlert.setContentText("Are you sure you want to delete this patient?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    patientService.deletePatient(patientId);
                    AlertUtils.showAlert("Success", "Patient deleted successfully", Alert.AlertType.INFORMATION);
                }
            }
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to delete patient: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Cannot delete patient: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void viewAllPatients() {
        viewPatients(); // Reuse the same logic
    }

    public void searchPatients() {
        Optional<String> result = patientFormView.showSearchDialog();
        if (result.isPresent()) {
            String searchTerm = result.get();
            try {
                List<Patient> patients = new ArrayList<>();

                // Helper in Service or here? Ideally Service handles "smart search",
                // but keeping it simple as logic routing for now.
                // Moving logic to specific checks.
                if (searchTerm.matches("\\d+")) {
                    Patient p = patientService.getPatientById(Integer.parseInt(searchTerm));
                    if (p != null)
                        patients.add(p);
                } else if (searchTerm.contains("@")) {
                    Patient p = patientService.getPatient(searchTerm);
                    if (p != null)
                        patients.add(p);
                } else {
                    patients = patientService.searchPatients(searchTerm);
                }

                if (patients.isEmpty()) {
                    AlertUtils.showAlert("No Results", "No patients found matching: " + searchTerm,
                            Alert.AlertType.INFORMATION);
                    return;
                }
                patientTableView.show("Search Results", patients, this::showMedicalHistory);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showMedicalHistory(Patient patient) {
        medicalHistoryView.show(patient, medicalLogDAO, prescriptionService);
    }
}
