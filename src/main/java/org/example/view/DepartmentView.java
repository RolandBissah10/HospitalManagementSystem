package org.example.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Department;
import org.example.util.AlertUtils;

import java.util.List;
import java.util.function.Consumer;

public class DepartmentView {

    public void show(List<Department> departments, Consumer<String> addHandler, Consumer<Integer> deleteHandler) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Departments");
            TableView<Department> table = new TableView<>(FXCollections.observableArrayList(departments));

            TableColumn<Department, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Department, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            table.getColumns().addAll(idCol, nameCol);

            VBox box = new VBox(10);
            Button addBtn = new Button("Add");
            addBtn.setOnAction(e -> showAddDialog(addHandler));

            // Context menu for delete/edit
            ContextMenu cm = new ContextMenu();
            MenuItem delItem = new MenuItem("Delete");
            delItem.setOnAction(e -> {
                Department d = table.getSelectionModel().getSelectedItem();
                if (d != null)
                    deleteHandler.accept(d.getId());
            });
            cm.getItems().add(delItem);
            table.setContextMenu(cm);

            box.getChildren().addAll(table, addBtn);
            box.setPadding(new Insets(10));
            stage.setScene(new Scene(box, 300, 400));
            stage.show();
        } catch (Exception e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void showAddDialog(Consumer<String> addHandler) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Add Dept");
        d.setHeaderText("Name:");
        d.showAndWait().ifPresent(addHandler);
    }
}
