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
import java.util.List;
import java.util.Optional;

public class MedicalLogController {

    private final MedicalLogDAO logDAO = new MedicalLogDAO();
    private final PatientService patientService = new PatientService();

    public void addMedicalLog() {
        // First select a patient
        TextInputDialog pDialog = new TextInputDialog();
        pDialog.setTitle("Add Medical Log");
        pDialog.setHeaderText("Enter Patient ID:");
        Optional<String> pResult = pDialog.showAndWait();

        if (pResult.isPresent() && !pResult.get().isEmpty()) {
            try {
                int patientId = Integer.parseInt(pResult.get());
                Patient p = patientService.getPatientById(patientId); // Just to verify existence
                if (p == null) {
                    AlertUtils.showAlert("Error", "Patient not found", Alert.AlertType.ERROR);
                    return;
                }

                // Show Log Input Dialog
                Dialog<MedicalLog> dialog = new Dialog<>();
                dialog.setTitle("New Medical Log");
                dialog.setHeaderText("Log for: " + p.getFirstName() + " " + p.getLastName());
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20));

                TextArea content = new TextArea();
                content.setPromptText("Enter detailed notes (NoSQL storage)...");
                ComboBox<String> severity = new ComboBox<>();
                severity.getItems().addAll("Routine", "Observation", "Critical");
                severity.setValue("Routine");

                grid.addRow(0, new Label("Log Content:"), content);
                grid.addRow(1, new Label("Severity:"), severity);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(b -> {
                    if (b == ButtonType.OK) {
                        return new MedicalLog(patientId, content.getText(), severity.getValue(), LocalDateTime.now());
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
        // No longer asking for Patient ID
        try {
            List<MedicalLog> logs = logDAO.getAllLogs();

            if (logs.isEmpty()) {
                AlertUtils.showAlert("Info", "No medical logs found.", Alert.AlertType.INFORMATION);
                return;
            }

            Stage stage = new Stage();
            stage.setTitle("All Medical Logs");

            TableView<MedicalLog> table = new TableView<>(FXCollections.observableArrayList(logs));

            // Added Patient ID column so we know who the log belongs to
            TableColumn<MedicalLog, Integer> patientIdCol = new TableColumn<>("Patient ID");
            patientIdCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
            patientIdCol.setPrefWidth(100);

            TableColumn<MedicalLog, String> dateCol = new TableColumn<>("Timestamp");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
            dateCol.setPrefWidth(150);

            TableColumn<MedicalLog, String> sevCol = new TableColumn<>("Severity");
            sevCol.setCellValueFactory(new PropertyValueFactory<>("severity"));

            TableColumn<MedicalLog, String> contentCol = new TableColumn<>("Content");
            contentCol.setCellValueFactory(new PropertyValueFactory<>("logContent"));
            contentCol.setPrefWidth(400);

            table.getColumns().addAll(patientIdCol, dateCol, sevCol, contentCol);

            stage.setScene(new Scene(table, 750, 400));
            stage.show();

        } catch (Exception e) {
            AlertUtils.showAlert("Connection Error", "Check mongodb+srv string\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }
}
