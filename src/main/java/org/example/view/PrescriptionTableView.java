package org.example.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Prescription;
import org.example.model.PrescriptionItem;
import org.example.service.PrescriptionService;
import org.example.util.AlertUtils;

import java.sql.SQLException;
import java.util.List;

public class PrescriptionTableView {

    public void show(List<Prescription> prescriptions, PrescriptionService prescriptionService) {
        Stage stage = new Stage();
        TableView<Prescription> table = new TableView<>(
                FXCollections.observableArrayList(prescriptions));

        TableColumn<Prescription, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Prescription, String> col1 = new TableColumn<>("Patient");
        col1.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getPatientName() != null ? c.getValue().getPatientName()
                        : String.valueOf(c.getValue().getPatientId())));

        TableColumn<Prescription, String> col2 = new TableColumn<>("Diagnosis");
        col2.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        table.getColumns().addAll(colId, col1, col2);

        // Add details button logic if needed, simplified for now
        table.setRowFactory(tv -> {
            TableRow<Prescription> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    viewPrescriptionDetails(row.getItem().getId(), prescriptionService);
                }
            });
            return row;
        });

        Scene scene = new Scene(table, 500, 400);
        stage.setTitle("Prescriptions");
        stage.setScene(scene);
        stage.show();
    }

    public void viewPrescriptionDetails(int id, PrescriptionService prescriptionService) {
        try {
            Prescription p = prescriptionService.getPrescription(id);
            List<PrescriptionItem> items = prescriptionService.getPrescriptionItems(id);
            Stage s = new Stage();
            s.setTitle("Details #" + id);
            VBox box = new VBox(10);
            box.setPadding(new Insets(10));
            box.getChildren().addAll(
                    new Label("Diagnosis: " + p.getDiagnosis()),
                    new Label("Notes: " + p.getNotes()),
                    new Label("Medications: " + items.size()));
            // List items
            for (PrescriptionItem i : items) {
                box.getChildren().add(new Label("- " + i.getMedication() + " (" + i.getDosage() + ")"));
            }
            s.setScene(new Scene(box, 300, 300));
            s.show();
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
