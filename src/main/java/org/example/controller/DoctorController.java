package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.dao.DoctorDAO;
import org.example.model.Department;
import org.example.model.Doctor;
import org.example.service.DoctorService;
import org.example.service.HospitalService;
import org.example.util.AlertUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DoctorController {

    private final DoctorService doctorService = new DoctorService();
    private final HospitalService hospitalService = new HospitalService();

    public void addDoctor() {
        try {
            List<Department> departments = hospitalService.getAllDepartments();
            if (departments.isEmpty()) {
                AlertUtils.showAlert("Error", "Please add at least one department first.", Alert.AlertType.WARNING);
                return;
            }

            Dialog<Doctor> dialog = new Dialog<>();
            dialog.setTitle("Add New Doctor");

            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField firstNameField = new TextField();
            TextField lastNameField = new TextField();
            TextField specialtyField = new TextField();
            ComboBox<Department> deptComboBox = new ComboBox<>(FXCollections.observableArrayList(departments));
            deptComboBox.setPromptText("Select Department");
            TextField phoneField = new TextField();
            TextField emailField = new TextField();

            grid.add(new Label("First Name*:"), 0, 0);
            grid.add(firstNameField, 1, 0);
            grid.add(new Label("Last Name*:"), 0, 1);
            grid.add(lastNameField, 1, 1);
            grid.add(new Label("Specialty*:"), 0, 2);
            grid.add(specialtyField, 1, 2);
            grid.add(new Label("Department*:"), 0, 3);
            grid.add(deptComboBox, 1, 3);
            grid.add(new Label("Phone*:"), 0, 4);
            grid.add(phoneField, 1, 4);
            grid.add(new Label("Email*:"), 0, 5);
            grid.add(emailField, 1, 5);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                            deptComboBox.getValue() == null || emailField.getText().isEmpty()) {
                        return null;
                    }

                    try {
                        if (doctorService.getDoctor(emailField.getText()) != null) {
                            AlertUtils.showAlert("Error", "Email already exists.", Alert.AlertType.ERROR);
                            return null;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }

                    return new Doctor(0, firstNameField.getText(), lastNameField.getText(),
                            specialtyField.getText(), deptComboBox.getValue().getId(),
                            phoneField.getText(), emailField.getText());
                }
                return null;
            });

            Optional<Doctor> result = dialog.showAndWait();
            result.ifPresent(doctor -> {
                try {
                    doctorService.addDoctor(doctor);
                    AlertUtils.showAlert("Success", "Doctor added.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", "Failed to load departments: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void viewDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            List<Department> departments = hospitalService.getAllDepartments();
            Map<Integer, String> deptMap = new HashMap<>();
            for (Department d : departments)
                deptMap.put(d.getId(), d.getName());

            Stage stage = new Stage();
            stage.setTitle("All Doctors");

            TableView<Doctor> tableView = new TableView<>();
            tableView.setItems(FXCollections.observableArrayList(doctors));

            TableColumn<Doctor, Integer> idColumn = new TableColumn<>("ID");
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            idColumn.setPrefWidth(50);

            TableColumn<Doctor, String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                    "Dr. " + cell.getValue().getFirstName() + " " + cell.getValue().getLastName()));
            nameColumn.setPrefWidth(180);

            TableColumn<Doctor, String> specialtyColumn = new TableColumn<>("Specialty");
            specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
            specialtyColumn.setPrefWidth(140);

            TableColumn<Doctor, String> deptColumn = new TableColumn<>("Department");
            deptColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                    deptMap.getOrDefault(cell.getValue().getDepartmentId(), "Unknown")));
            deptColumn.setPrefWidth(140);

            TableColumn<Doctor, String> phoneColumn = new TableColumn<>("Phone");
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            phoneColumn.setPrefWidth(130);

            TableColumn<Doctor, String> emailColumn = new TableColumn<>("Email");
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            emailColumn.setPrefWidth(180);

            tableView.getColumns().addAll(idColumn, nameColumn, specialtyColumn, deptColumn, phoneColumn, emailColumn);

            Scene scene = new Scene(tableView, 750, 400);
            stage.setScene(scene);
            stage.show();

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

                Dialog<Doctor> updateDialog = new Dialog<>();
                updateDialog.setTitle("Update Doctor");
                updateDialog
                        .setHeaderText("Update details for Dr. " + doctor.getFirstName() + " " + doctor.getLastName());

                ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
                updateDialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField firstNameField = new TextField(doctor.getFirstName());
                TextField lastNameField = new TextField(doctor.getLastName());
                TextField specialtyField = new TextField(doctor.getSpecialty());
                ComboBox<Department> deptComboBox = new ComboBox<>(FXCollections.observableArrayList(departments));

                for (Department d : departments) {
                    if (d.getId() == doctor.getDepartmentId()) {
                        deptComboBox.setValue(d);
                        break;
                    }
                }

                TextField phoneField = new TextField(doctor.getPhone());
                TextField emailField = new TextField(doctor.getEmail());

                grid.add(new Label("First Name:"), 0, 0);
                grid.add(firstNameField, 1, 0);
                grid.add(new Label("Last Name:"), 0, 1);
                grid.add(lastNameField, 1, 1);
                grid.add(new Label("Specialty:"), 0, 2);
                grid.add(specialtyField, 1, 2);
                grid.add(new Label("Department:"), 0, 3);
                grid.add(deptComboBox, 1, 3);
                grid.add(new Label("Phone:"), 0, 4);
                grid.add(phoneField, 1, 4);
                grid.add(new Label("Email:"), 0, 5);
                grid.add(emailField, 1, 5);

                updateDialog.getDialogPane().setContent(grid);

                updateDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == updateButtonType) {
                        doctor.setFirstName(firstNameField.getText());
                        doctor.setLastName(lastNameField.getText());
                        doctor.setSpecialty(specialtyField.getText());
                        if (deptComboBox.getValue() != null) {
                            doctor.setDepartmentId(deptComboBox.getValue().getId());
                        }
                        doctor.setPhone(phoneField.getText());
                        doctor.setEmail(emailField.getText());
                        return doctor;
                    }
                    return null;
                });

                Optional<Doctor> result = updateDialog.showAndWait();
                result.ifPresent(updatedDoctor -> {
                    try {
                        String originalEmail = doctorEmail;
                        doctorService.updateDoctor(updatedDoctor, originalEmail);
                        AlertUtils.showAlert("Success", "Doctor updated.", Alert.AlertType.INFORMATION);
                    } catch (SQLException e) {
                        AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                    }
                });
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

                Stage stage = new Stage();
                stage.setTitle("Search Results for Doctors");

                TableView<Doctor> tableView = new TableView<>();
                ObservableList<Doctor> doctorList = FXCollections.observableArrayList(doctors);
                tableView.setItems(doctorList);

                TableColumn<Doctor, Integer> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                idColumn.setPrefWidth(50);

                TableColumn<Doctor, String> nameColumn = new TableColumn<>("Name");
                nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                        "Dr. " + cell.getValue().getFirstName() + " " + cell.getValue().getLastName()));
                nameColumn.setPrefWidth(200);

                TableColumn<Doctor, String> specialtyColumn = new TableColumn<>("Specialty");
                specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
                specialtyColumn.setPrefWidth(180);

                tableView.getColumns().addAll(idColumn, nameColumn, specialtyColumn);

                Scene scene = new Scene(tableView, 400, 300);
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {
                AlertUtils.showAlert("Error", "Search failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}
