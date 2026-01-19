package org.example.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.model.Patient;
import org.example.model.PatientFeedback;
import org.example.service.PatientService;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;
import org.example.validation.FeedbackValidator;
import org.example.validation.InputValidator;

import java.util.List;
import java.util.Optional;

public class FeedbackView {

    private final FeedbackValidator feedbackValidator = new FeedbackValidator();

    public void show(List<PatientFeedback> feedbackList) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Feedback");
            TableView<PatientFeedback> table = new TableView<>(FXCollections.observableArrayList(feedbackList));

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
        } catch (Exception e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public Optional<PatientFeedback> showFeedbackDialog(PatientService patientService) {
        Dialog<PatientFeedback> dialog = new Dialog<>();
        dialog.setTitle("Feedback");
        dialog.setHeaderText("Submit your feedback");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField email = new TextField();
        email.setPromptText("Email");
        TextField appId = new TextField();
        appId.setPromptText("Appointment ID (Optional)");

        ComboBox<String> ratingCombo = new ComboBox<>(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
        ratingCombo.setValue("5");
        ratingCombo.setEditable(false);

        TextArea comments = new TextArea();
        comments.setPromptText("Max 500 characters");
        comments.setWrapText(true);
        comments.setPrefRowCount(3);

        Label emailError = createErrorLabel();
        Label appIdError = createErrorLabel();
        Label ratingError = createErrorLabel();
        Label openCommentsError = createErrorLabel();

        grid.addRow(0, new Label("Your Email*:"), email);
        grid.add(emailError, 2, 0);

        grid.addRow(1, new Label("Appointment ID:"), appId);
        grid.add(appIdError, 2, 1);

        grid.addRow(2, new Label("Rating*:"), ratingCombo);
        grid.add(ratingError, 2, 2);

        grid.addRow(3, new Label("Comments:"), comments);
        grid.add(openCommentsError, 2, 3);

        Label helpText = new Label("* Required fields");
        helpText.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
        grid.add(helpText, 0, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Input Validation
        InputValidator.bind(email, ValidationUtils::isValidEmail, emailError, "Invalid email format");
        InputValidator.bind(appId, (val) -> val.isEmpty() || val.matches("\\d+"), appIdError, "Must be a number");
        InputValidator.bind(comments, (val) -> val.length() <= 500, openCommentsError, "Too long");

        dialog.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    String rStr = ratingCombo.getValue();
                    List<String> errors = feedbackValidator.validate(rStr, comments.getText());

                    if (!ValidationUtils.isValidEmail(email.getText())) {
                        errors.add("Invalid email address.");
                    }

                    if (!errors.isEmpty()) {
                        AlertUtils.showAlert("Validation Error", String.join("\n", errors), Alert.AlertType.ERROR);
                        return null;
                    }

                    Patient p = patientService.getPatient(email.getText());
                    if (p == null) {
                        AlertUtils.showAlert("Error", "Patient not found with email: " + email.getText(),
                                Alert.AlertType.ERROR);
                        return null;
                    }

                    PatientFeedback f = new PatientFeedback();
                    f.setPatientId(p.getId());
                    if (!appId.getText().isEmpty()) {
                        f.setAppointmentId(Integer.parseInt(appId.getText()));
                    }
                    f.setRating(Integer.parseInt(rStr));
                    f.setComments(comments.getText());
                    return f;
                } catch (Exception e) {
                    AlertUtils.showAlert("Error", "Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
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
