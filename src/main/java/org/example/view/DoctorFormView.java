package org.example.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.model.Department;
import org.example.model.Doctor;
import org.example.service.DoctorService;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;
import org.example.validation.DoctorValidator;
import org.example.validation.InputValidator;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DoctorFormView {

    private final DoctorValidator doctorValidator = new DoctorValidator();

    public void showAddDoctorDialog(DoctorService doctorService, List<Department> departments) {
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

        // Error labels
        Label firstNameError = createErrorLabel();
        Label lastNameError = createErrorLabel();
        Label specialtyError = createErrorLabel();
        Label phoneError = createErrorLabel();
        Label emailError = createErrorLabel();

        grid.add(new Label("First Name*:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(firstNameError, 2, 0);

        grid.add(new Label("Last Name*:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(lastNameError, 2, 1);

        grid.add(new Label("Specialty*:"), 0, 2);
        grid.add(specialtyField, 1, 2);
        grid.add(specialtyError, 2, 2);

        grid.add(new Label("Department*:"), 0, 3);
        grid.add(deptComboBox, 1, 3);

        grid.add(new Label("Phone*:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(phoneError, 2, 4);

        grid.add(new Label("Email*:"), 0, 5);
        grid.add(emailField, 1, 5);
        grid.add(emailError, 2, 5);

        dialog.getDialogPane().setContent(grid);

        // Bind validation
        InputValidator.bind(firstNameField, ValidationUtils::isValidName, firstNameError, "Invalid first name");
        InputValidator.bind(lastNameField, ValidationUtils::isValidName, lastNameError, "Invalid last name");
        InputValidator.bind(specialtyField, ValidationUtils::isValidSpecialty, specialtyError, "Invalid specialty");
        InputValidator.bind(phoneField, ValidationUtils::isValidPhone, phoneError, "Invalid phone format");
        InputValidator.bind(emailField, ValidationUtils::isValidEmail, emailError, "Invalid email format");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {

                List<String> errors = doctorValidator.validate(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        specialtyField.getText(),
                        deptComboBox.getValue(),
                        phoneField.getText(),
                        emailField.getText(),
                        doctorService);

                if (!errors.isEmpty()) {
                    AlertUtils.showAlert("Validation Error", String.join("\n", errors), Alert.AlertType.ERROR);
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
    }

    public void showUpdateDoctorDialog(Doctor doctor, DoctorService doctorService, List<Department> departments,
            String originalEmail) {
        Dialog<Doctor> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update Doctor");
        updateDialog.setHeaderText("Update details for Dr. " + doctor.getFirstName() + " " + doctor.getLastName());

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
                DoctorService serviceToCheck = null;
                if (!emailField.getText().equals(originalEmail)) {
                    serviceToCheck = doctorService;
                }

                List<String> errors = doctorValidator.validate(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        specialtyField.getText(),
                        deptComboBox.getValue(),
                        phoneField.getText(),
                        emailField.getText(),
                        serviceToCheck);

                if (!errors.isEmpty()) {
                    AlertUtils.showAlert("Validation Error", String.join("\n", errors), Alert.AlertType.ERROR);
                    return null;
                }

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
                doctorService.updateDoctor(updatedDoctor, originalEmail);
                AlertUtils.showAlert("Success", "Doctor updated.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private Label createErrorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        return label;
    }
}
