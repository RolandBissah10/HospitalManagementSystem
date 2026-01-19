package org.example.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.model.Appointment;
import org.example.model.Doctor;
import org.example.model.Patient;
import org.example.service.DoctorService;
import org.example.service.PatientService;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;
import org.example.validation.AppointmentValidator;
import org.example.validation.InputValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class AppointmentFormView {

    private final AppointmentValidator appointmentValidator = new AppointmentValidator();

    public Optional<Appointment> showScheduleDialog(PatientService patientService, DoctorService doctorService) {
        Dialog<Appointment> dialog = new Dialog<>();
        dialog.setTitle("Schedule Appointment");
        dialog.setHeaderText("Enter appointment details");

        ButtonType scheduleButtonType = new ButtonType("Schedule", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(scheduleButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField patientEmailField = new TextField();
        patientEmailField.setPromptText("Patient Email");
        TextField doctorEmailField = new TextField();
        doctorEmailField.setPromptText("Doctor Email");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        TextField timeField = new TextField();
        timeField.setPromptText("Time (HH:MM)");
        timeField.setText("09:00");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("scheduled", "completed", "cancelled");
        statusCombo.setValue("scheduled");
        TextField reasonField = new TextField();
        reasonField.setPromptText("Reason (optional)");

        // Error labels
        Label patientEmailError = createErrorLabel();
        Label doctorEmailError = createErrorLabel();
        Label timeError = createErrorLabel();

        grid.add(new Label("Patient Email*:"), 0, 0);
        grid.add(patientEmailField, 1, 0);
        grid.add(patientEmailError, 2, 0);

        grid.add(new Label("Doctor Email*:"), 0, 1);
        grid.add(doctorEmailField, 1, 1);
        grid.add(doctorEmailError, 2, 1);

        grid.add(new Label("Date*:"), 0, 2);
        grid.add(datePicker, 1, 2);

        grid.add(new Label("Time* (HH:MM):"), 0, 3);
        grid.add(timeField, 1, 3);
        grid.add(timeError, 2, 3);

        grid.add(new Label("Status*:"), 0, 4);
        grid.add(statusCombo, 1, 4);

        grid.add(new Label("Reason:"), 0, 5);
        grid.add(reasonField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Bind validation
        InputValidator.bind(patientEmailField, ValidationUtils::isValidEmail, patientEmailError,
                "Invalid email format");
        InputValidator.bind(doctorEmailField, ValidationUtils::isValidEmail, doctorEmailError, "Invalid email format");
        InputValidator.bind(timeField, ValidationUtils::isValidTime, timeError, "Format: HH:MM");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == scheduleButtonType) {
                try {
                    String pEmail = patientEmailField.getText().trim();
                    String dEmail = doctorEmailField.getText().trim();
                    String dateStr = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";
                    String timeText = timeField.getText().trim();
                    if (timeText.split(":").length == 2) {
                        timeText += ":00";
                    }

                    // Lookup entities
                    // Note: Ideally validation shouldn't do deep service lookups if we want purely
                    // stateless validation,
                    // but here we need to check existence.
                    Patient p = null;
                    Doctor d = null;
                    try {
                        if (!pEmail.isEmpty())
                            p = patientService.getPatient(pEmail);
                        if (!dEmail.isEmpty())
                            d = doctorService.getDoctor(dEmail);
                    } catch (Exception e) {
                        // ignore, null check will catch it
                    }

                    List<String> errors = appointmentValidator.validate(p, d, dateStr, timeText, reasonField.getText());
                    // Add specific message if lookup failed but email format was valid
                    if (p == null && errors.contains("Please select a patient.") && !pEmail.isEmpty()) {
                        errors.remove("Please select a patient.");
                        errors.add("Patient not found with email: " + pEmail);
                    }
                    if (d == null && errors.contains("Please select a doctor.") && !dEmail.isEmpty()) {
                        errors.remove("Please select a doctor.");
                        errors.add("Doctor not found with email: " + dEmail);
                    }

                    if (!errors.isEmpty()) {
                        AlertUtils.showAlert("Validation Error", String.join("\n", errors), Alert.AlertType.ERROR);
                        return null;
                    }

                    Appointment appointment = new Appointment();
                    appointment.setPatientId(p.getId());
                    appointment.setDoctorId(d.getId());
                    appointment.setAppointmentDate(datePicker.getValue());
                    appointment.setAppointmentTime(LocalTime.parse(timeText));
                    appointment.setStatus(statusCombo.getValue());
                    appointment.setReason(reasonField.getText().trim());
                    return appointment;
                } catch (Exception e) {
                    AlertUtils.showAlert("Input Error", e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public Optional<Appointment> showUpdateDialog(Appointment appointment) {
        Dialog<Appointment> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update Appointment");
        updateDialog.setHeaderText("Update Appointment #" + appointment.getId());

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        updateDialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker(appointment.getAppointmentDate());
        TextField timeField = new TextField(appointment.getAppointmentTime().toString());
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("scheduled", "completed", "cancelled");
        statusCombo.setValue(appointment.getStatus());

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(timeField, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusCombo, 1, 2);

        updateDialog.getDialogPane().setContent(grid);

        updateDialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String dateStr = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";
                String timeText = timeField.getText().trim();
                if (timeText.length() == 5)
                    timeText += ":00";

                // Reuse validator, passing placeholders for readonly fields
                // Actually update dialog usually implies minimal validation or just Date/Time
                // validation.
                // We can use helper checks.
                if (!ValidationUtils.isValidDate(dateStr) || !ValidationUtils.isValidTime(timeText)) {
                    AlertUtils.showAlert("Error", "Invalid Date or Time format", Alert.AlertType.ERROR);
                    return null;
                }

                appointment.setAppointmentDate(datePicker.getValue());
                appointment.setAppointmentTime(LocalTime.parse(timeText));
                appointment.setStatus(statusCombo.getValue());
                return appointment;
            }
            return null;
        });

        return updateDialog.showAndWait();
    }

    private Label createErrorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        return label;
    }
}
