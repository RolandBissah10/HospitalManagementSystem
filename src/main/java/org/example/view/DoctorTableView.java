package org.example.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Department;
import org.example.model.Doctor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorTableView {

    public void show(String title, List<Doctor> doctors, List<Department> departments) {
        Map<Integer, String> deptMap = new HashMap<>();
        if (departments != null) {
            for (Department d : departments)
                deptMap.put(d.getId(), d.getName());
        }

        Stage stage = new Stage();
        stage.setTitle(title);

        TableView<Doctor> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(doctors));

        TableColumn<Doctor, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        TableColumn<Doctor, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                "Dr. " + cell.getValue().getFirstName() + " " + cell.getValue().getLastName()));
        nameColumn.setPrefWidth(180);

        TableColumn<Doctor, String> specialtyColumn = new TableColumn<>("Specialty");
        specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        specialtyColumn.setPrefWidth(140);

        TableColumn<Doctor, String> deptColumn = new TableColumn<>("Department");
        deptColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                deptMap.getOrDefault(cell.getValue().getDepartmentId(), "Unknown")));
        deptColumn.setPrefWidth(140);

        TableColumn<Doctor, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setPrefWidth(130);

        TableColumn<Doctor, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(180);

        tableView.getColumns().addAll(idColumn, nameColumn, specialtyColumn, deptColumn, phoneColumn, emailColumn);

        Scene scene = new Scene(tableView, 750, 400);
        stage.setScene(scene);
        stage.show();
    }
}
