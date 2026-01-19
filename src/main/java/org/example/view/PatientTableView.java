package org.example.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Patient;

import java.util.List;
import java.util.function.Consumer;

public class PatientTableView {

    public void show(String title, List<Patient> patients, Consumer<Patient> onHistoryClick) {
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
                    if (onHistoryClick != null) {
                        onHistoryClick.accept(patient);
                    }
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
}
