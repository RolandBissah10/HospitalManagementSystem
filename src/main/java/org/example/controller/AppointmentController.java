package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.model.Appointment;
import org.example.service.AppointmentService;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;

import org.example.model.Doctor;
import org.example.model.Patient;
import org.example.service.DoctorService;
import org.example.service.PatientService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AppointmentController {

    private final AppointmentService appointmentService = new AppointmentService();
    private final PatientService patientService = new PatientService();
    private final DoctorService doctorService = new DoctorService();

    public void scheduleAppointment() {
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

        grid.add(new Label("Patient Email*:"), 0, 0);
        grid.add(patientEmailField, 1, 0);
        grid.add(new Label("Doctor Email*:"), 0, 1);
        grid.add(doctorEmailField, 1, 1);
        grid.add(new Label("Date*:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Time* (HH:MM):"), 0, 3);
        grid.add(timeField, 1, 3);
        grid.add(new Label("Status*:"), 0, 4);
        grid.add(statusCombo, 1, 4);
        grid.add(new Label("Reason:"), 0, 5);
        grid.add(reasonField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == scheduleButtonType) {
                try {
                    String pEmail = patientEmailField.getText().trim();
                    String dEmail = doctorEmailField.getText().trim();

                    Patient p = patientService.getPatient(pEmail);
                    if (p == null) {
                        throw new Exception("Patient not found with email: " + pEmail);
                    }

                    Doctor d = doctorService.getDoctor(dEmail);
                    if (d == null) {
                        throw new Exception("Doctor not found with email: " + dEmail);
                    }

                    Appointment appointment = new Appointment();
                    appointment.setPatientId(p.getId());
                    appointment.setDoctorId(d.getId());
                    appointment.setAppointmentDate(datePicker.getValue());

                    String timeText = timeField.getText().trim();
                    if (timeText.split(":").length == 2) {
                        timeText += ":00";
                    }
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

        Optional<Appointment> result = dialog.showAndWait();
        result.ifPresent(appointment -> {
            try {
                appointmentService.addAppointment(appointment);
                AlertUtils.showAlert("Success", "Appointment scheduled successfully", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Database Error", "Failed to schedule appointment: " + e.getMessage(),
                        Alert.AlertType.ERROR);
            }
        });
    }

    public void viewAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            showAppointmentTable("All Appointments", appointments);
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to load appointments: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    public void updateAppointment() {
        try {
            TextInputDialog idDialog = new TextInputDialog();
            idDialog.setTitle("Update Appointment");
            idDialog.setHeaderText("Enter Appointment ID to update:");
            idDialog.setContentText("Appointment ID:");

            Optional<String> idResult = idDialog.showAndWait();
            if (idResult.isPresent() && !idResult.get().isEmpty()) {
                if (!ValidationUtils.isValidId(idResult.get())) {
                    AlertUtils.showAlert("Validation Error", "Invalid Appointment ID", Alert.AlertType.ERROR);
                    return;
                }

                int appointmentId = Integer.parseInt(idResult.get());
                Appointment appointment = appointmentService.getAppointment(appointmentId);
                if (appointment == null) {
                    AlertUtils.showAlert("Not Found", "Appointment not found.", Alert.AlertType.ERROR);
                    return;
                }

                Dialog<Appointment> updateDialog = new Dialog<>();
                updateDialog.setTitle("Update Appointment");
                updateDialog.setHeaderText("Update Appointment #" + appointmentId);

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
                        appointment.setAppointmentDate(datePicker.getValue());
                        // Simplified parse for brevity in this task view
                        String timeText = timeField.getText().trim();
                        if (timeText.length() == 5)
                            timeText += ":00";
                        appointment.setAppointmentTime(LocalTime.parse(timeText));
                        appointment.setStatus(statusCombo.getValue());
                        return appointment;
                    }
                    return null;
                });

                Optional<Appointment> result = updateDialog.showAndWait();
                result.ifPresent(updatedAppointment -> {
                    try {
                        appointmentService.updateAppointment(updatedAppointment);
                        AlertUtils.showAlert("Success", "Appointment updated.", Alert.AlertType.INFORMATION);
                    } catch (SQLException e) {
                        AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                    }
                });
            }
        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Error updating appointment: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void deleteAppointment() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Delete Appointment");
            dialog.setContentText("Appointment ID:");

            Optional<String> idResult = dialog.showAndWait();
            if (idResult.isPresent() && !idResult.get().isEmpty()) {
                int appointmentId = Integer.parseInt(idResult.get());
                appointmentService.deleteAppointment(appointmentId);
                AlertUtils.showAlert("Success", "Appointment deleted.", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Cannot delete: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void searchAppointments() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Appointments");
        dialog.setHeaderText("Search by Patient or Doctor Name");
        dialog.setContentText("Enter name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            String searchTerm = result.get().trim();
            try {
                List<Appointment> allAppointments = appointmentService.getAllAppointments();
                List<Appointment> filtered = allAppointments.stream()
                        .filter(a -> (a.getPatientName() != null
                                && a.getPatientName().toLowerCase().contains(searchTerm.toLowerCase())) ||
                                (a.getDoctorName() != null
                                        && a.getDoctorName().toLowerCase().contains(searchTerm.toLowerCase())))
                        .toList();

                if (filtered.isEmpty()) {
                    AlertUtils.showAlert("No Results", "No appointments found.", Alert.AlertType.INFORMATION);
                    return;
                }
                showAppointmentTable("Search Results", filtered);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showAppointmentTable(String title, List<Appointment> appointments) {
        Stage stage = new Stage();
        stage.setTitle(title);
        TableView<Appointment> table = new TableView<>();
        ObservableList<Appointment> data = FXCollections.observableArrayList(appointments);

        TableColumn<Appointment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Appointment, String> patCol = new TableColumn<>("Patient");
        patCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        patCol.setPrefWidth(150);

        TableColumn<Appointment, String> docCol = new TableColumn<>("Doctor");
        docCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        docCol.setPrefWidth(150);

        TableColumn<Appointment, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        dateCol.setPrefWidth(110);

        TableColumn<Appointment, LocalTime> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        timeCol.setPrefWidth(90);

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, patCol, docCol, dateCol, timeCol, statusCol);
        table.setItems(data);

        Scene scene = new Scene(table, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}
