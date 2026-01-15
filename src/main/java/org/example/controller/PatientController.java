package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Patient;
import org.example.service.PatientService;
import org.example.service.PrescriptionService;
import org.example.dao.MedicalLogDAO;
import org.example.model.MedicalLog;
import org.example.model.Prescription;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientController {

    private final PatientService patientService = new PatientService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final MedicalLogDAO medicalLogDAO = new MedicalLogDAO();

    // The methods below are copied and adapted from MainController

    public void addPatient() {
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
        Label firstNameError = new Label();
        firstNameError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        Label lastNameError = new Label();
        lastNameError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        Label phoneError = new Label();
        phoneError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        Label emailError = new Label();
        emailError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        Label addressError = new Label();
        addressError.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

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
        firstNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!ValidationUtils.isValidName(newVal)) {
                firstNameError.setText("Invalid first name");
            } else {
                firstNameError.setText("");
            }
        });

        lastNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!ValidationUtils.isValidName(newVal)) {
                lastNameError.setText("Invalid last name");
            } else {
                lastNameError.setText("");
            }
        });

        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!ValidationUtils.isValidPhone(newVal)) {
                phoneError.setText("Invalid phone format");
            } else {
                phoneError.setText("");
            }
        });

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !ValidationUtils.isValidEmail(newVal)) {
                emailError.setText("Invalid email format");
            } else {
                emailError.setText("");
            }
        });

        addressField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !ValidationUtils.isValidAddress(newVal)) {
                addressError.setText("Invalid address");
            } else {
                addressError.setText("");
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                // Validate all fields
                boolean isValid = true;
                StringBuilder errorMessage = new StringBuilder();

                if (!ValidationUtils.isValidName(firstNameField.getText())) {
                    isValid = false;
                    errorMessage.append(ValidationUtils.getNameErrorMessage()).append("\n");
                }

                if (!ValidationUtils.isValidName(lastNameField.getText())) {
                    isValid = false;
                    errorMessage.append(ValidationUtils.getNameErrorMessage()).append("\n");
                }

                if (!ValidationUtils.isValidPhone(phoneField.getText())) {
                    isValid = false;
                    errorMessage.append(ValidationUtils.getPhoneErrorMessage()).append("\n");
                }

                if (!emailField.getText().isEmpty()) {
                    if (!ValidationUtils.isValidEmail(emailField.getText())) {
                        isValid = false;
                        errorMessage.append(ValidationUtils.getEmailErrorMessage()).append("\n");
                    } else {
                        try {
                            if (patientService.getPatient(emailField.getText()) != null) {
                                isValid = false;
                                errorMessage.append("Email already exists.").append("\n");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (!addressField.getText().isEmpty() && !ValidationUtils.isValidAddress(addressField.getText())) {
                    isValid = false;
                    errorMessage.append(ValidationUtils.getAddressErrorMessage()).append("\n");
                }

                if (!isValid) {
                    AlertUtils.showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
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

        Optional<Patient> result = dialog.showAndWait();
        result.ifPresent(patient -> {
            try {
                patientService.addPatient(patient);
                AlertUtils.showAlert("Success", "Patient added successfully (ID: " + patient.getId() + ")",
                        Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Database Error", "Failed to add patient: " + e.getMessage(),
                        Alert.AlertType.ERROR);
            }
        });
    }

    public void viewPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            showPatientTable("All Patients", patients);
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to load patients: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void updatePatient() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Update Patient");
            dialog.setHeaderText("Enter Patient email to update:");
            dialog.setContentText("Patient Email:");

            Optional<String> emailResult = dialog.showAndWait();
            if (emailResult.isPresent() && !emailResult.get().isEmpty()) {
                String patientEmail = emailResult.get().trim();

                Patient patient = patientService.getPatient(patientEmail);
                if (patient == null) {
                    AlertUtils.showAlert("Not Found", "Patient with email " + patientEmail + " not found.",
                            Alert.AlertType.ERROR);
                    return;
                }

                Dialog<Patient> updateDialog = new Dialog<>();
                updateDialog.setTitle("Update Patient");
                updateDialog
                        .setHeaderText("Update details for " + patient.getFirstName() + " " + patient.getLastName());

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
                        String originalEmail = patientEmail;
                        patientService.updatePatient(updatedPatient, originalEmail);
                        AlertUtils.showAlert("Success", "Patient updated successfully!", Alert.AlertType.INFORMATION);
                    } catch (SQLException e) {
                        AlertUtils.showAlert("Database Error", "Failed to update patient: " + e.getMessage(),
                                Alert.AlertType.ERROR);
                    }
                });
            }
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to update patient: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            AlertUtils.showAlert("Input Error", "Please enter a valid email address: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    public void deletePatient() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Delete Patient");
            dialog.setHeaderText("Enter Patient ID to delete:");
            dialog.setContentText("Patient ID:");

            Optional<String> idResult = dialog.showAndWait();
            if (idResult.isPresent() && !idResult.get().isEmpty()) {
                int patientId = Integer.parseInt(idResult.get());

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText("Delete Patient ID: " + patientId);
                confirmAlert.setContentText("Are you sure you want to delete this patient?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    patientService.deletePatient(patientId);
                    AlertUtils.showAlert("Success", "Patient deleted successfully", Alert.AlertType.INFORMATION);
                }
            }
        } catch (NumberFormatException e) {
            AlertUtils.showAlert("Input Error", "Please enter a valid numeric ID", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to delete patient: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Cannot delete patient: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void viewAllPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            showPatientTable("All Patients", patients);
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", "Failed to fetch patients: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void searchPatients() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Search Patients");
        dialog.setHeaderText("Search by Name, Email, or ID");

        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField searchField = new TextField();
        searchField.setPromptText("Enter Name, Email, or ID");
        content.getChildren().setAll(new Label("Search Query:"), searchField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(b -> b == searchButtonType ? searchField.getText() : null);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            String searchTerm = result.get().trim();
            try {
                List<Patient> patients = new ArrayList<>();

                // Check if search term is an ID (integer)
                if (searchTerm.matches("\\d+")) {
                    Patient p = patientService.getPatientById(Integer.parseInt(searchTerm));
                    if (p != null)
                        patients.add(p);
                }
                // Check if search term is an Email
                else if (searchTerm.contains("@")) {
                    Patient p = patientService.getPatient(searchTerm);
                    if (p != null)
                        patients.add(p);
                }
                // Search by Name
                else {
                    patients = patientService.searchPatients(searchTerm);
                }

                if (patients.isEmpty()) {
                    AlertUtils.showAlert("No Results", "No patients found matching: " + searchTerm,
                            Alert.AlertType.INFORMATION);
                    return;
                }
                showPatientTable("Search Results", patients);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showPatientTable(String title, List<Patient> patients) {
        Stage stage = new Stage();
        stage.setTitle(title);

        TableView<Patient> tableView = new TableView<>();
        ObservableList<Patient> patientList = FXCollections.observableArrayList(patients);
        tableView.setItems(patientList);

        TableColumn<Patient, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        TableColumn<Patient, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setPrefWidth(100);

        TableColumn<Patient, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameColumn.setPrefWidth(100);

        // Add Actions column
        TableColumn<Patient, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewHistoryBtn = new Button("View History");
            {
                viewHistoryBtn.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    showMedicalHistory(patient);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else
                    setGraphic(viewHistoryBtn);
            }
        });

        tableView.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, actionsColumn);
        Scene scene = new Scene(tableView, 800, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showMedicalHistory(Patient patient) {
        Stage historyStage = new Stage();
        historyStage.setTitle("Medical History - " + patient.getFirstName() + " " + patient.getLastName());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Medical Logs (MongoDB)
        Tab logsTab = new Tab("Medical Logs");
        TableView<MedicalLog> logsTable = new TableView<>();

        TableColumn<MedicalLog, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTimestamp().toString()));

        TableColumn<MedicalLog, String> severityCol = new TableColumn<>("Severity");
        severityCol.setCellValueFactory(new PropertyValueFactory<>("severity"));

        TableColumn<MedicalLog, String> contentCol = new TableColumn<>("Content");
        contentCol.setCellValueFactory(new PropertyValueFactory<>("logContent"));

        logsTable.getColumns().addAll(dateCol, severityCol, contentCol);

        // Fetch logs
        try {
            logsTable.setItems(FXCollections.observableArrayList(medicalLogDAO.getLogsByPatientId(patient.getId())));
        } catch (Exception e) {
            logsTable.setPlaceholder(new Label("Error fetching logs: " + e.getMessage()));
        }

        logsTab.setContent(logsTable);

        // Tab 2: Prescriptions (MySQL)
        Tab prescriptionsTab = new Tab("Prescriptions");
        TableView<Prescription> prescriptionsTable = new TableView<>();

        TableColumn<Prescription, String> pDateCol = new TableColumn<>("Date");
        pDateCol.setCellValueFactory(new PropertyValueFactory<>("prescriptionDate"));

        TableColumn<Prescription, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        TableColumn<Prescription, String> diagnosisCol = new TableColumn<>("Diagnosis");
        diagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        TableColumn<Prescription, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        prescriptionsTable.getColumns().addAll(pDateCol, doctorCol, diagnosisCol, notesCol);

        // Fetch prescriptions
        try {
            prescriptionsTable.setItems(FXCollections.observableArrayList(
                    prescriptionService.getPrescriptionsByPatient(patient.getId())));
        } catch (SQLException e) {
            prescriptionsTable.setPlaceholder(new Label("Error: " + e.getMessage()));
        }

        prescriptionsTab.setContent(prescriptionsTable);

        tabPane.getTabs().addAll(logsTab, prescriptionsTab);

        Scene scene = new Scene(tabPane, 800, 600);
        historyStage.setScene(scene);
        historyStage.show();
    }
}
