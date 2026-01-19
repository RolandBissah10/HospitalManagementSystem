package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import org.example.dao.DoctorDAO;
import org.example.model.Department;
import org.example.model.Doctor;
import org.example.service.DoctorService;
import org.example.service.HospitalService;
import org.example.util.AlertUtils;
import org.example.view.DoctorFormView;
import org.example.view.DoctorTableView;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DoctorController {

    private final DoctorService doctorService = new DoctorService();
    private final HospitalService hospitalService = new HospitalService();

    // Views
    private final DoctorFormView doctorFormView = new DoctorFormView();
    private final DoctorTableView doctorTableView = new DoctorTableView();

    public void addDoctor() {
        try {
            List<Department> departments = hospitalService.getAllDepartments();
            // Validation handles empty department selection
            doctorFormView.showAddDoctorDialog(doctorService, departments);
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", "Failed to load departments: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void viewDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            List<Department> departments = hospitalService.getAllDepartments();
            doctorTableView.show("All Doctors", doctors, departments);
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to load doctors: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void updateDoctor() {
        try {
            TextInputDialog emailDialog = new TextInputDialog();
            emailDialog.setTitle("Update Doctor");
            emailDialog.setHeaderText("Enter Doctor email to update:");
            emailDialog.setContentText("Doctor Email:");

            Optional<String> emailResult = emailDialog.showAndWait();
            if (emailResult.isPresent() && !emailResult.get().isEmpty()) {
                String doctorEmail = emailResult.get().trim();

                Doctor doctor = doctorService.getDoctor(doctorEmail);
                if (doctor == null) {
                    AlertUtils.showAlert("Not Found", "Doctor with email " + doctorEmail + " not found.",
                            Alert.AlertType.ERROR);
                    return;
                }

                List<Department> departments = hospitalService.getAllDepartments();
                doctorFormView.showUpdateDoctorDialog(doctor, doctorService, departments, doctorEmail);
            }
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to update doctor: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            AlertUtils.showAlert("Input Error", "Please enter a valid email address: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    public void deleteDoctor() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Delete Doctor");
            dialog.setHeaderText("Enter Doctor email to delete:");
            dialog.setContentText("Doctor Email:");

            Optional<String> emailResult = dialog.showAndWait();
            if (emailResult.isPresent() && !emailResult.get().isEmpty()) {
                String doctorEmail = emailResult.get().trim();
                Doctor doctor = doctorService.getDoctor(doctorEmail);
                if (doctor == null) {
                    AlertUtils.showAlert("Not Found", "Doctor with email " + doctorEmail + " not found.",
                            Alert.AlertType.ERROR);
                    return;
                }

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText("Delete Doctor Email: " + doctorEmail);
                confirmAlert.setContentText("Are you sure you want to delete Dr. " +
                        doctor.getFirstName() + " " + doctor.getLastName() + "?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    doctorService.deleteDoctor(doctor.getId());
                    AlertUtils.showAlert("Success", "Doctor deleted successfully!", Alert.AlertType.INFORMATION);
                }
            }
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to delete doctor: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Cannot delete doctor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void searchDoctors() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Doctors");
        dialog.setHeaderText("Search by name or specialty");
        dialog.setContentText("Enter search term:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            try {
                DoctorDAO doctorDAO = new DoctorDAO();
                List<Doctor> doctors = doctorDAO.searchDoctors(result.get());

                if (doctors.isEmpty()) {
                    AlertUtils.showAlert("No Results", "No doctors found.", Alert.AlertType.INFORMATION);
                    return;
                }

                // Pass null for departments if not strictly needed for basic search view,
                // or fetch them if expanding the view to show department names
                doctorTableView.show("Search Results for Doctors", doctors, null);

            } catch (Exception e) {
                AlertUtils.showAlert("Error", "Search failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}
