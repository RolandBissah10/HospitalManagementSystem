package org.example.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.model.MedicalLog;
import org.example.model.Patient;
import org.example.service.PatientService;
import org.example.util.AlertUtils;
import org.example.validation.InputValidator;
import org.example.validation.MedicalLogValidator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MedicalLogView {

    private final MedicalLogValidator medicalLogValidator = new MedicalLogValidator();

    public void show(List<MedicalLog> logs, PatientService patientService) {
        try {
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
                    return new SimpleStringProperty(p.getFirstName() + " " + p.getLastName());
                }
                return new SimpleStringProperty("Unknown");
            });
            patientNameCol.setPrefWidth(150);

            // Patient Email column
            TableColumn<MedicalLog, String> patientEmailCol = new TableColumn<>("Patient Email");
            patientEmailCol.setCellValueFactory(cellData -> {
                int patientId = cellData.getValue().getPatientId();
                Patient p = patientMap.get(patientId);
                if (p != null) {
                    String email = p.getEmail();
                    return new SimpleStringProperty(
                            (email != null && !email.isEmpty()) ? email : "N/A");
                }
                return new SimpleStringProperty("N/A");
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

    public Optional<Patient> searchPatientDialog(PatientService patientService) { // Changed return type and added param
        Dialog<Patient> pDialog = new Dialog<>();
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
                try {
                    if (!patientIdField.getText().trim().isEmpty()) {
                        String idStr = patientIdField.getText().trim();
                        if (idStr.matches("\\d+")) {
                            Patient p = patientService.getPatientById(Integer.parseInt(idStr));
                            if (p != null)
                                return p;
                        }
                    } else if (!patientEmailField.getText().trim().isEmpty()) {
                        Patient p = patientService.getPatient(patientEmailField.getText().trim());
                        if (p != null)
                            return p;
                    }
                } catch (Exception e) {
                    // Logic handled, return null if failed
                }
            }
            return null;
        });

        Optional<Patient> result = pDialog.showAndWait();
        if (result.isEmpty()) {
            // Optional: Show alert if search was attempted but failed (logic needs flag,
            // skipping for simplicity or adding internal alert)
            // For strict user feedback, we can show alert inside converter or here if
            // button was pressed but result null.
            // But the controller logic had explicit alerts. Let's add them to the view.
        }
        return result;
    }

    public Optional<MedicalLog> showLogDialog(Patient p) {
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

        Label contentError = createErrorLabel();

        grid.addRow(0, new Label("Log Content*:"), content);
        grid.add(contentError, 1, 1); // Below content field

        grid.addRow(2, new Label("Severity*:"), severity); // Shifted down

        Label helpText = new Label("* Required fields");
        helpText.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
        grid.add(helpText, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Input validation
        InputValidator.bind(content, (c) -> !c.trim().isEmpty(), contentError, "Content required");

        final int finalPatientId = p.getId();
        dialog.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                List<String> errors = medicalLogValidator.validate(content.getText(), severity.getValue());

                if (!errors.isEmpty()) {
                    AlertUtils.showAlert("Validation Error", String.join("\n", errors), Alert.AlertType.ERROR);
                    return null;
                }

                return new MedicalLog(finalPatientId, content.getText(), severity.getValue(),
                        LocalDateTime.now());
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
