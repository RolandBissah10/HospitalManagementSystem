package org.example.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Department;
import org.example.service.HospitalService;
import org.example.util.AlertUtils;

import java.sql.SQLException;
import java.util.Optional;

public class DepartmentController {
    private final HospitalService hospitalService = new HospitalService();

    public void viewDepartments() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Departments");
            TableView<Department> table = new TableView<>(
                    FXCollections.observableArrayList(hospitalService.getAllDepartments()));

            TableColumn<Department, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Department, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            table.getColumns().addAll(idCol, nameCol);

            VBox box = new VBox(10);
            Button addBtn = new Button("Add");
            addBtn.setOnAction(e -> addDepartment());

            // Context menu for delete/edit
            ContextMenu cm = new ContextMenu();
            MenuItem delItem = new MenuItem("Delete");
            delItem.setOnAction(e -> {
                Department d = table.getSelectionModel().getSelectedItem();
                if (d != null)
                    deleteDepartment(d.getId());
            });
            cm.getItems().add(delItem);
            table.setContextMenu(cm);

            box.getChildren().addAll(table, addBtn);
            box.setPadding(new Insets(10));
            stage.setScene(new Scene(box, 300, 400));
            stage.show();
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void addDepartment() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Add Dept");
        d.setHeaderText("Name:");
        d.showAndWait().ifPresent(name -> {
            try {
                hospitalService.addDepartment(new Department(0, name));
                AlertUtils.showAlert("Success", "Added", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    public void deleteDepartment(int id) {
        try {
            hospitalService.deleteDepartment(id);
            AlertUtils.showAlert("Success", "Deleted", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
