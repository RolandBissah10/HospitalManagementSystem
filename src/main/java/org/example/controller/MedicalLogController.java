package org.example.controller;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import org.example.dao.MedicalLogDAO;
import org.example.model.Patient;
import org.example.service.PatientService;
import org.example.util.AlertUtils;
import org.example.view.MedicalLogView;

import java.util.Optional;

public class MedicalLogController {

    private final MedicalLogDAO logDAO = new MedicalLogDAO();
    private final PatientService patientService = new PatientService();
    private final MedicalLogView medicalLogView = new MedicalLogView();

    public void addMedicalLog() {
        // View handles search and returns a valid Patient object if found
        Optional<Patient> pResult = medicalLogView.searchPatientDialog(patientService);

        pResult.ifPresent(p -> {
            medicalLogView.showLogDialog(p).ifPresent(log -> {
                try {
                    logDAO.addLog(log);
                    AlertUtils.showAlert("Success", "Log saved to MongoDB!", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    AlertUtils.showAlert("Connection Error", "Check mongodb+srv string\n" + e.getMessage(),
                            Alert.AlertType.ERROR);
                }
            });
        });

        // Note: View shows "Not Found" logic or simply returns empty if cancelled/not
        // found.
        // If we want "Patient not found" alert, we can check if result is empty AND
        // button was search (requires complex view logic).
        // For now, removing the *Validation* logic from Controller is the goal.
    }

    public void viewMedicalLogs() {
        try {
            medicalLogView.show(logDAO.getAllLogs(), patientService);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to load medical logs:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }
}
