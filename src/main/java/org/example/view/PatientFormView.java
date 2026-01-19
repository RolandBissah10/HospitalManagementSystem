package org.example.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.model.Patient;
import org.example.service.PatientService;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;
import org.example.validation.InputValidator;
import org.example.validation.PatientValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PatientFormView {

    private final PatientValidator patientValidator = new PatientValidator();

    public Optional<Patient> showAddPatientDialog(PatientService patientService) {
        Dialog<Patient> dialog = new Dialog<>();
        dialog.setTitle("Add New Patient");
        dialog.setHeaderText("Enter patient details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        DatePicker dobPicker = new DatePicker();
        dobPicker.setValue(LocalDate.now().minusYears(30));
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone (e.g., 123-456-7890)");
        TextField emailField = new TextField();
        emailField.setPromptText("Email (optional)");

        // Add validation labels
        Label firstNameError = createErrorLabel();
        Label lastNameError = createErrorLabel();
        Label phoneError = createErrorLabel();
        Label emailError = createErrorLabel();
        Label addressError = createErrorLabel();

        grid.add(new Label("First Name*:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(firstNameError, 2, 0);

        grid.add(new Label("Last Name*:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(lastNameError, 2, 1);

        grid.add(new Label("Date of Birth*:"), 0, 2);
        grid.add(dobPicker, 1, 2);

        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(addressError, 2, 3);

        grid.add(new Label("Phone*:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(phoneError, 2, 4);

        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);
        grid.add(emailError, 2, 5);

        dialog.getDialogPane().setContent(grid);

        // Add real-time validation
        InputValidator.bind(firstNameField, ValidationUtils::isValidName, firstNameError, "Invalid first name");
        InputValidator.bind(lastNameField, ValidationUtils::isValidName, lastNameError, "Invalid last name");
        InputValidator.bind(phoneField, ValidationUtils::isValidPhone, phoneError, "Invalid phone format");
        InputValidator.bindOptional(emailField, ValidationUtils::isValidEmail, emailError, "Invalid email format");
        InputValidator.bindOptional(addressField, ValidationUtils::isValidAddress, addressError, "Invalid address");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                // Validate all fields
                List<String> errors = patientValidator.validate(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        addressField.getText(),
                        patientService // Pass service to check for duplicates
                );

                if (!errors.isEmpty()) {
                    AlertUtils.showAlert("Validation Error", String.join("\n", errors), Alert.AlertType.ERROR);
                    return null;
                }

                Patient patient = new Patient();
                patient.setFirstName(firstNameField.getText().trim());
                patient.setLastName(lastNameField.getText().trim());
                patient.setDateOfBirth(dobPicker.getValue());
                patient.setAddress(addressField.getText().trim());
                patient.setPhone(phoneField.getText().trim());
                patient.setEmail(emailField.getText().trim());
                return patient;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public void showUpdatePatientDialog(Patient patient, PatientService patientService, String originalEmail,
            Runnable onSuccess) {
        Dialog<Patient> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update Patient");
        updateDialog.setHeaderText("Update details for " + patient.getFirstName() + " " + patient.getLastName());

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        updateDialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstNameField = new TextField(patient.getFirstName());
        TextField lastNameField = new TextField(patient.getLastName());
        DatePicker dobPicker = new DatePicker(patient.getDateOfBirth());
        TextField addressField = new TextField(patient.getAddress());
        TextField phoneField = new TextField(patient.getPhone());
        TextField emailField = new TextField(patient.getEmail());

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Date of Birth:"), 0, 2);
        grid.add(dobPicker, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);

        updateDialog.getDialogPane().setContent(grid);

        updateDialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                // Check if email changed and if it's unique
                PatientService serviceToCheck = null;
                if (!emailField.getText().equals(originalEmail)) {
                    serviceToCheck = patientService;
                }

                List<String> errors = patientValidator.validate(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        addressField.getText(),
                        serviceToCheck);

                if (!errors.isEmpty()) {
                    AlertUtils.showAlert("Validation Error", String.join("\n", errors), Alert.AlertType.ERROR);
                    return null;
                }

                patient.setFirstName(firstNameField.getText());
                patient.setLastName(lastNameField.getText());
                patient.setDateOfBirth(dobPicker.getValue());
                patient.setAddress(addressField.getText());
                patient.setPhone(phoneField.getText());
                patient.setEmail(emailField.getText());
                return patient;
            }
            return null;
        });

        Optional<Patient> result = updateDialog.showAndWait();
        result.ifPresent(updatedPatient -> {
            try {
                patientService.updatePatient(updatedPatient, originalEmail);
                AlertUtils.showAlert("Success", "Patient updated successfully!", Alert.AlertType.INFORMATION);
                if (onSuccess != null)
                    onSuccess.run();
            } catch (SQLException e) {
                AlertUtils.showAlert("Database Error", "Failed to update patient: " + e.getMessage(),
                        Alert.AlertType.ERROR);
            }
        });
    }

    public Optional<Integer> askForPatientId(String title) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText("Enter Patient ID:");
        dialog.setContentText("ID:");

        while (true) {
            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty())
                return Optional.empty();

            String input = result.get().trim();
            if (input.isEmpty())
                return Optional.empty();

            if (ValidationUtils.isValidId(input)) {
                return Optional.of(Integer.parseInt(input));
            } else {
                AlertUtils.showAlert("Invalid Input", "Please enter a valid numeric ID.", Alert.AlertType.ERROR);
            }
        }
    }

    public Optional<String> showSearchDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Search Patients");
        dialog.setHeaderText("Search by Name, Email, or ID");

        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.setPadding(new Insets(20));

        TextField searchField = new TextField();
        searchField.setPromptText("Enter Name, Email, or ID");
        content.getChildren().setAll(new Label("Search Query:"), searchField);
        dialog.getDialogPane().setContent(content);

        // Simple input validation visual
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        content.getChildren().add(errorLabel);

        InputValidator.bind(searchField, (s) -> !s.trim().isEmpty(), errorLabel, "Search term required");

        dialog.setResultConverter(b -> {
            if (b == searchButtonType && !searchField.getText().trim().isEmpty()) {
                return searchField.getText().trim();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Label createErrorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        return label;
    }
}
