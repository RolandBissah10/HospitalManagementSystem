package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Prescription;
import org.example.model.PrescriptionItem;
import org.example.model.Doctor;
import org.example.model.Patient;
import org.example.service.DoctorService;
import org.example.service.PatientService;
import org.example.service.PrescriptionService;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class PrescriptionController {
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final PatientService patientService = new PatientService();
    private final DoctorService doctorService = new DoctorService();

    public void addPrescription() {
        Dialog<Map<String, Object>> dialog = createPrescriptionDialog("Add New Prescription", null, null);
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

            Dialog<Map<String, Object>> dialog = createPrescriptionDialog("Update Prescription", p, items);
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

    // Helper to reduce code duplication and keep size down
    private Dialog<Map<String, Object>> createPrescriptionDialog(String title, Prescription existing,
            List<PrescriptionItem> existingItems) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle(title);
        ButtonType okParams = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okParams, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        String patEmail = "";
        String docEmail = "";

        // Reverse lookup if editing
        if (existing != null) {
            try {
                Patient p = patientService.getPatientById(existing.getPatientId());
                if (p != null)
                    patEmail = p.getEmail();
                Doctor d = doctorService.getDoctorById(existing.getDoctorId());
                if (d != null)
                    docEmail = d.getEmail();
            } catch (SQLException e) {
                // Ignore errors here, just leave empty
            }
        }

        TextField patEmailField = new TextField(patEmail);
        patEmailField.setPromptText("Patient Email");
        TextField docEmailField = new TextField(docEmail);
        docEmailField.setPromptText("Doctor Email");
        DatePicker dateVal = new DatePicker(existing != null ? existing.getPrescriptionDate() : LocalDate.now());
        TextArea diag = new TextArea(existing != null ? existing.getDiagnosis() : "");
        diag.setPrefRowCount(2);
        TextArea notes = new TextArea(existing != null ? existing.getNotes() : "");
        notes.setPrefRowCount(2);

        PrescriptionItem firstItem = (existingItems != null && !existingItems.isEmpty()) ? existingItems.get(0) : null;
        TextField med = new TextField(firstItem != null ? firstItem.getMedication() : "");
        TextField dose = new TextField(firstItem != null ? firstItem.getDosage() : "");

        grid.addRow(0, new Label("Patient Email:"), patEmailField);
        grid.addRow(1, new Label("Doctor Email:"), docEmailField);
        grid.addRow(2, new Label("Date:"), dateVal);
        grid.addRow(3, new Label("Diagnosis:"), diag);
        grid.addRow(4, new Label("Notes:"), notes);
        grid.addRow(5, new Label("Medication:"), med);
        grid.addRow(6, new Label("Dosage:"), dose);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(b -> {
            if (b == okParams) {
                try {
                    String pEmail = patEmailField.getText().trim();
                    String dEmail = docEmailField.getText().trim();

                    Patient p = patientService.getPatient(pEmail);
                    if (p == null)
                        throw new Exception("Patient not found: " + pEmail);

                    Doctor d = doctorService.getDoctor(dEmail);
                    if (d == null)
                        throw new Exception("Doctor not found: " + dEmail);

                    Prescription pObj = existing != null ? existing : new Prescription();
                    pObj.setPatientId(p.getId());
                    pObj.setDoctorId(d.getId());
                    pObj.setPrescriptionDate(dateVal.getValue());
                    pObj.setDiagnosis(diag.getText());
                    pObj.setNotes(notes.getText());

                    PrescriptionItem item = new PrescriptionItem();
                    item.setMedication(med.getText());
                    item.setDosage(dose.getText());
                    return Map.of("prescription", pObj, "items", List.of(item));
                } catch (Exception e) {
                    AlertUtils.showAlert("Invalid Input", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        return dialog;
    }

    public void viewPrescriptions() {
        try {
            Stage stage = new Stage();
            TableView<Prescription> table = new TableView<>(
                    FXCollections.observableArrayList(prescriptionService.getAllPrescriptions()));

            TableColumn<Prescription, Integer> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colId.setPrefWidth(50);

            TableColumn<Prescription, String> col1 = new TableColumn<>("Patient");
            col1.setCellValueFactory(
                    c -> new SimpleStringProperty(c.getValue().getPatientName() != null ? c.getValue().getPatientName()
                            : String.valueOf(c.getValue().getPatientId())));

            TableColumn<Prescription, String> col2 = new TableColumn<>("Diagnosis");
            col2.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

            table.getColumns().addAll(colId, col1, col2);

            // Add details button logic if needed, simplified for now
            table.setRowFactory(tv -> {
                TableRow<Prescription> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        viewPrescriptionDetails(row.getItem().getId());
                    }
                });
                return row;
            });

            Scene scene = new Scene(table, 500, 400);
            stage.setTitle("Prescriptions");
            stage.setScene(scene);
            stage.show();
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

    public void viewPrescriptionDetails(int id) {
        try {
            Prescription p = prescriptionService.getPrescription(id);
            List<PrescriptionItem> items = prescriptionService.getPrescriptionItems(id);
            Stage s = new Stage();
            s.setTitle("Details #" + id);
            VBox box = new VBox(10);
            box.setPadding(new Insets(10));
            box.getChildren().addAll(
                    new Label("Diagnosis: " + p.getDiagnosis()),
                    new Label("Notes: " + p.getNotes()),
                    new Label("Medications: " + items.size()));
            // List items
            for (PrescriptionItem i : items) {
                box.getChildren().add(new Label("- " + i.getMedication() + " (" + i.getDosage() + ")"));
            }
            s.setScene(new Scene(box, 300, 300));
            s.show();
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
