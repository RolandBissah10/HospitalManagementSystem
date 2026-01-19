package org.example.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.model.Doctor;
import org.example.model.Patient;
import org.example.model.Prescription;
import org.example.model.PrescriptionItem;
import org.example.service.DoctorService;
import org.example.service.PatientService;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;
import org.example.validation.InputValidator;
import org.example.validation.PrescriptionValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PrescriptionFormView {

    private final PrescriptionValidator prescriptionValidator = new PrescriptionValidator();

    public Dialog<Map<String, Object>> createPrescriptionDialog(String title, Prescription existing,
            List<PrescriptionItem> existingItems,
            PatientService patientService,
            DoctorService doctorService) {
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
        TextField duration = new TextField(firstItem != null ? String.valueOf(firstItem.getDurationDays()) : "");
        duration.setPromptText("Days (e.g. 7)");
        TextField frequency = new TextField(firstItem != null ? firstItem.getFrequency() : "");
        frequency.setPromptText("e.g. 3 times daily");

        Label patEmailError = createErrorLabel();
        Label docEmailError = createErrorLabel();
        Label medError = createErrorLabel();
        Label doseError = createErrorLabel();
        Label durationError = createErrorLabel();

        grid.addRow(0, new Label("Patient Email:"), patEmailField);
        grid.add(patEmailError, 2, 0);

        grid.addRow(1, new Label("Doctor Email:"), docEmailField);
        grid.add(docEmailError, 2, 1);

        grid.addRow(2, new Label("Date:"), dateVal);
        grid.addRow(3, new Label("Diagnosis:"), diag);
        grid.addRow(4, new Label("Notes:"), notes);
        grid.addRow(5, new Label("Medication:"), med);
        grid.add(medError, 2, 5);

        grid.addRow(6, new Label("Dosage:"), dose);
        grid.add(doseError, 2, 6);

        grid.addRow(7, new Label("Duration (Days):"), duration);
        grid.add(durationError, 2, 7);

        grid.addRow(8, new Label("Frequency:"), frequency);

        dialog.getDialogPane().setContent(grid);

        // Bind real-time validation
        InputValidator.bind(patEmailField, ValidationUtils::isValidEmail, patEmailError, "Invalid email format");
        InputValidator.bind(docEmailField, ValidationUtils::isValidEmail, docEmailError, "Invalid email format");
        InputValidator.bind(med, ValidationUtils::isValidMedication, medError,
                ValidationUtils.getMedicationErrorMessage());
        InputValidator.bind(dose, ValidationUtils::isValidDosage, doseError, ValidationUtils.getDosageErrorMessage());
        InputValidator.bind(duration, (s) -> {
            try {
                return Integer.parseInt(s) > 0;
            } catch (Exception e) {
                return false;
            }
        }, durationError, "Must be positive integer");

        dialog.setResultConverter(b -> {
            if (b == okParams) {
                try {
                    String pEmail = patEmailField.getText().trim();
                    String dEmail = docEmailField.getText().trim();

                    Patient p = patientService.getPatient(pEmail);
                    Doctor d = doctorService.getDoctor(dEmail);

                    List<String> errors = prescriptionValidator.validate(p, d, med.getText(), dose.getText(),
                            duration.getText());
                    if (!errors.isEmpty()) {
                        AlertUtils.showAlert("Validation Error", String.join("\n", errors), Alert.AlertType.ERROR);
                        return null;
                    }

                    // Specific user not found errors if validate passed general checks but objects
                    // are null
                    // Actually validator checks if p/d are null.

                    Prescription pObj = existing != null ? existing : new Prescription();
                    pObj.setPatientId(p.getId());
                    pObj.setDoctorId(d.getId());
                    pObj.setPrescriptionDate(dateVal.getValue());
                    pObj.setDiagnosis(diag.getText());
                    pObj.setNotes(notes.getText());

                    PrescriptionItem item = new PrescriptionItem();
                    item.setMedication(med.getText());
                    item.setDosage(dose.getText());
                    try {
                        item.setDurationDays(Integer.parseInt(duration.getText().trim()));
                    } catch (NumberFormatException e) {
                        // Should be caught by validator but safe fallback
                        item.setDurationDays(0);
                    }
                    item.setFrequency(frequency.getText());

                    return Map.of("prescription", pObj, "items", Collections.singletonList(item));
                } catch (SQLException e) {
                    AlertUtils.showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
                } catch (Exception e) {
                    AlertUtils.showAlert("Invalid Input", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        return dialog;
    }

    private Label createErrorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        return label;
    }
}
