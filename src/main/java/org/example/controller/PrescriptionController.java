package org.example.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import org.example.model.Prescription;
import org.example.model.PrescriptionItem;
import org.example.service.DoctorService;
import org.example.service.PatientService;
import org.example.service.PrescriptionService;
import org.example.util.AlertUtils;
import org.example.view.PrescriptionFormView;
import org.example.view.PrescriptionTableView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PrescriptionController {
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final PatientService patientService = new PatientService();
    private final DoctorService doctorService = new DoctorService();

    // Views
    private final PrescriptionFormView prescriptionFormView = new PrescriptionFormView();
    private final PrescriptionTableView prescriptionTableView = new PrescriptionTableView();

    @SuppressWarnings("unchecked")
    public void addPrescription() {
        Dialog<Map<String, Object>> dialog = prescriptionFormView.createPrescriptionDialog("Add New Prescription", null,
                null, patientService, doctorService);
        dialog.showAndWait().ifPresent(data -> {
            try {
                Prescription p = (Prescription) data.get("prescription");
                List<PrescriptionItem> items = (List<PrescriptionItem>) data.get("items");
                prescriptionService.addPrescription(p, items);
                AlertUtils.showAlert("Success", "Prescription added!", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void updatePrescription() {
        try {
            TextInputDialog idDialog = new TextInputDialog();
            idDialog.setTitle("Update Prescription");
            idDialog.setContentText("Prescription ID:");
            Optional<String> idResult = idDialog.showAndWait();
            if (idResult.isEmpty())
                return;

            int pid = Integer.parseInt(idResult.get());
            Prescription p = prescriptionService.getPrescription(pid);
            if (p == null) {
                AlertUtils.showAlert("Error", "Not found", Alert.AlertType.ERROR);
                return;
            }
            List<PrescriptionItem> items = prescriptionService.getPrescriptionItems(pid);

            Dialog<Map<String, Object>> dialog = prescriptionFormView.createPrescriptionDialog("Update Prescription", p,
                    items, patientService, doctorService);
            dialog.showAndWait().ifPresent(data -> {
                try {
                    prescriptionService.updatePrescription((Prescription) data.get("prescription"),
                            (List<PrescriptionItem>) data.get("items"));
                    AlertUtils.showAlert("Success", "Updated!", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            });

        } catch (Exception e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void viewPrescriptions() {
        try {
            prescriptionTableView.show(prescriptionService.getAllPrescriptions(), prescriptionService);
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void deletePrescription() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Delete Prescription");
        d.setContentText("ID:");
        d.showAndWait().ifPresent(id -> {
            try {
                prescriptionService.deletePrescription(Integer.parseInt(id));
                AlertUtils.showAlert("Success", "Deleted", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
