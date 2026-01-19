package org.example.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import org.example.model.Appointment;
import org.example.service.AppointmentService;
import org.example.service.DoctorService;
import org.example.service.PatientService;
import org.example.util.AlertUtils;
import org.example.view.AppointmentFormView;
import org.example.view.AppointmentTableView;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AppointmentController {

    private final AppointmentService appointmentService = new AppointmentService();
    private final PatientService patientService = new PatientService();
    private final DoctorService doctorService = new DoctorService();

    // Views
    private final AppointmentFormView appointmentFormView = new AppointmentFormView();
    private final AppointmentTableView appointmentTableView = new AppointmentTableView();

    public void scheduleAppointment() {
        Optional<Appointment> result = appointmentFormView.showScheduleDialog(patientService, doctorService);
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
            appointmentTableView.show("All Appointments", appointments);
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
                // Delegation: Let parsing/service handle validity or move this dialog to View
                // later
                try {
                    int appointmentId = Integer.parseInt(idResult.get());
                    Appointment appointment = appointmentService.getAppointment(appointmentId);
                    if (appointment == null) {
                        AlertUtils.showAlert("Not Found", "Appointment not found.", Alert.AlertType.ERROR);
                        return;
                    }

                    Optional<Appointment> result = appointmentFormView.showUpdateDialog(appointment);
                    result.ifPresent(updatedAppointment -> {
                        try {
                            appointmentService.updateAppointment(updatedAppointment);
                            AlertUtils.showAlert("Success", "Appointment updated.", Alert.AlertType.INFORMATION);
                        } catch (SQLException e) {
                            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                        }
                    });
                } catch (NumberFormatException e) {
                    AlertUtils.showAlert("Invalid Input", "Appointment ID must be a number.", Alert.AlertType.ERROR);
                }
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
                appointmentTableView.show("Search Results", filtered);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}
