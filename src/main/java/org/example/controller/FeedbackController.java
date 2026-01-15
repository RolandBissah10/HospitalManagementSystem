package org.example.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.model.Patient;
import org.example.model.PatientFeedback;
import org.example.service.HospitalService;
import org.example.service.PatientService;
import org.example.util.AlertUtils;

import java.sql.SQLException;

public class FeedbackController {
    private final HospitalService hospitalService = new HospitalService();
    private final PatientService patientService = new PatientService();

    public void viewPatientFeedback() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Feedback");
            TableView<PatientFeedback> table = new TableView<>(
                    FXCollections.observableArrayList(hospitalService.getAllFeedback()));

            TableColumn<PatientFeedback, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            idCol.setPrefWidth(50);

            TableColumn<PatientFeedback, Integer> patIdCol = new TableColumn<>("Patient ID");
            patIdCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
            patIdCol.setPrefWidth(70);

            TableColumn<PatientFeedback, Integer> appIdCol = new TableColumn<>("Appointment ID");
            appIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            appIdCol.setPrefWidth(70);

            TableColumn<PatientFeedback, Integer> ratCol = new TableColumn<>("Rating");
            ratCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
            ratCol.setPrefWidth(60);

            TableColumn<PatientFeedback, String> comCol = new TableColumn<>("Comments");
            comCol.setCellValueFactory(new PropertyValueFactory<>("comments"));

            table.getColumns().addAll(idCol, patIdCol, appIdCol, ratCol, comCol);
            stage.setScene(new Scene(table, 500, 400));
            stage.show();
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void showFeedbackDialog() {
        Dialog<PatientFeedback> dialog = new Dialog<>();
        dialog.setTitle("Feedback");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField email = new TextField();
        email.setPromptText("Email");
        TextField appId = new TextField();
        appId.setPromptText("Appointment ID (Optional)");
        ComboBox<Integer> rating = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        rating.setValue(5);
        TextArea comments = new TextArea();

        grid.addRow(0, new Label("Email:"), email);
        grid.addRow(1, new Label("Appointment ID:"), appId);
        grid.addRow(2, new Label("Rating:"), rating);
        grid.addRow(3, new Label("Comments:"), comments);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    Patient p = patientService.getPatient(email.getText());
                    if (p == null)
                        return null;

                    PatientFeedback f = new PatientFeedback();
                    f.setPatientId(p.getId());
                    if (!appId.getText().isEmpty())
                        f.setAppointmentId(Integer.parseInt(appId.getText()));
                    f.setRating(rating.getValue());
                    f.setComments(comments.getText());
                    return f;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(f -> {
            try {
                hospitalService.addFeedback(f);
                AlertUtils.showAlert("Success", "Feedback Sent", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
