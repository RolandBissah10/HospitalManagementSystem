package org.example.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dao.MedicalLogDAO;
import org.example.model.MedicalLog;
import org.example.model.Patient;
import org.example.service.PatientService;
import org.example.util.AlertUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MedicalLogController {

    private final MedicalLogDAO logDAO = new MedicalLogDAO();
    private final PatientService patientService = new PatientService();

    public void addMedicalLog() {
        // First select a patient by ID or Email
        Dialog<String> pDialog = new Dialog<>();
        pDialog.setTitle("Add Medical Log");
        pDialog.setHeaderText("Enter Patient ID or Email:");

        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        pDialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        GridPane searchGrid = new GridPane();
        searchGrid.setHgap(10);
        searchGrid.setVgap(10);
        searchGrid.setPadding(new Insets(20));

        TextField patientIdField = new TextField();
        patientIdField.setPromptText("Patient ID");
        TextField patientEmailField = new TextField();
        patientEmailField.setPromptText("Patient Email");

        searchGrid.addRow(0, new Label("Patient ID:"), patientIdField);
        searchGrid.addRow(1, new Label("OR Email:"), patientEmailField);
        searchGrid.addRow(2, new Label("(Enter either ID or Email)"));

        pDialog.getDialogPane().setContent(searchGrid);
        pDialog.setResultConverter(b -> {
            if (b == searchButtonType) {
                if (!patientIdField.getText().trim().isEmpty()) {
                    return "ID:" + patientIdField.getText().trim();
                } else if (!patientEmailField.getText().trim().isEmpty()) {
                    return "EMAIL:" + patientEmailField.getText().trim();
                }
            }
            return null;
        });

        Optional<String> pResult = pDialog.showAndWait();

        if (pResult.isPresent() && pResult.get() != null) {
            try {
                Patient p = null;
                String searchValue = pResult.get();

                if (searchValue.startsWith("ID:")) {
                    int patientId = Integer.parseInt(searchValue.substring(3));
                    p = patientService.getPatientById(patientId);
                } else if (searchValue.startsWith("EMAIL:")) {
                    String email = searchValue.substring(6);
                    p = patientService.getPatient(email);
                }

                if (p == null) {
                    AlertUtils.showAlert("Error", "Patient not found", Alert.AlertType.ERROR);
                    return;
                }

                // Show Log Input Dialog
                Dialog<MedicalLog> dialog = new Dialog<>();
                dialog.setTitle("New Medical Log");
                dialog.setHeaderText("Log for: " + p.getFirstName() + " " + p.getLastName() +
                        "\nID: " + p.getId() + " | Email: " + p.getEmail());
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20));

                TextArea content = new TextArea();
                content.setPromptText("Enter detailed notes (NoSQL storage)...");
                content.setPrefRowCount(5);
                content.setPrefColumnCount(40);

                ComboBox<String> severity = new ComboBox<>();
                severity.getItems().addAll("Routine", "Observation", "Critical");
                severity.setValue("Routine");

                grid.addRow(0, new Label("Log Content:"), content);
                grid.addRow(1, new Label("Severity:"), severity);

                dialog.getDialogPane().setContent(grid);

                final int finalPatientId = p.getId();
                dialog.setResultConverter(b -> {
                    if (b == ButtonType.OK) {
                        return new MedicalLog(finalPatientId, content.getText(), severity.getValue(),
                                LocalDateTime.now());
                    }
                    return null;
                });

                dialog.showAndWait().ifPresent(log -> {
                    try {
                        logDAO.addLog(log);
                        AlertUtils.showAlert("Success", "Log saved to MongoDB!", Alert.AlertType.INFORMATION);
                    } catch (Exception e) {
                        AlertUtils.showAlert("Connection Error", "Check mongodb+srv string\n" + e.getMessage(),
                                Alert.AlertType.ERROR);
                    }
                });

            } catch (NumberFormatException e) {
                AlertUtils.showAlert("Error", "Invalid ID", Alert.AlertType.ERROR);
            } catch (Exception e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    public void viewMedicalLogs() {
        try {
            List<MedicalLog> logs = logDAO.getAllLogs();

            if (logs.isEmpty()) {
                AlertUtils.showAlert("Info", "No medical logs found.", Alert.AlertType.INFORMATION);
                return;
            }

            // Pre-fetch all patient data to avoid multiple database calls
            Map<Integer, Patient> patientMap = new HashMap<>();

            for (MedicalLog log : logs) {
                try {
                    int patientId = log.getPatientId();
                    Patient p = patientService.getPatientById(patientId);
                    if (p != null) {
                        patientMap.put(patientId, p);
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching patient " + log.getPatientId() + ": " + e.getMessage());
                }
            }

            Stage stage = new Stage();
            stage.setTitle("Medical Logs - " + logs.size() + " records");

            TableView<MedicalLog> table = new TableView<>(FXCollections.observableArrayList(logs));

            // Patient ID column
            TableColumn<MedicalLog, Integer> patientIdCol = new TableColumn<>("Patient ID");
            patientIdCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
            patientIdCol.setPrefWidth(80);

            // Patient Name column
            TableColumn<MedicalLog, String> patientNameCol = new TableColumn<>("Patient Name");
            patientNameCol.setCellValueFactory(cellData -> {
                int patientId = cellData.getValue().getPatientId();
                Patient p = patientMap.get(patientId);
                if (p != null) {
                    return new javafx.beans.property.SimpleStringProperty(p.getFirstName() + " " + p.getLastName());
                }
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            });
            patientNameCol.setPrefWidth(150);

            // Patient Email column
            TableColumn<MedicalLog, String> patientEmailCol = new TableColumn<>("Patient Email");
            patientEmailCol.setCellValueFactory(cellData -> {
                int patientId = cellData.getValue().getPatientId();
                Patient p = patientMap.get(patientId);
                if (p != null) {
                    String email = p.getEmail();
                    return new javafx.beans.property.SimpleStringProperty(
                            (email != null && !email.isEmpty()) ? email : "N/A");
                }
                return new javafx.beans.property.SimpleStringProperty("N/A");
            });
            patientEmailCol.setPrefWidth(200);

            // Timestamp column
            TableColumn<MedicalLog, String> dateCol = new TableColumn<>("Timestamp");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
            dateCol.setPrefWidth(160);

            // Severity column
            TableColumn<MedicalLog, String> sevCol = new TableColumn<>("Severity");
            sevCol.setCellValueFactory(new PropertyValueFactory<>("severity"));
            sevCol.setPrefWidth(100);

            // Content column
            TableColumn<MedicalLog, String> contentCol = new TableColumn<>("Log Content");
            contentCol.setCellValueFactory(new PropertyValueFactory<>("logContent"));
            contentCol.setPrefWidth(350);

            table.getColumns().addAll(patientIdCol, patientNameCol, patientEmailCol, dateCol, sevCol, contentCol);

            stage.setScene(new Scene(table, 1100, 500));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to load medical logs:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }
}
