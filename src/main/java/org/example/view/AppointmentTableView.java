package org.example.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentTableView {

    public void show(String title, List<Appointment> appointments) {
        Stage stage = new Stage();
        stage.setTitle(title);
        TableView<Appointment> table = new TableView<>();
        ObservableList<Appointment> data = FXCollections.observableArrayList(appointments);

        TableColumn<Appointment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Appointment, String> patCol = new TableColumn<>("Patient");
        patCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        patCol.setPrefWidth(150);

        TableColumn<Appointment, String> docCol = new TableColumn<>("Doctor");
        docCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        docCol.setPrefWidth(150);

        TableColumn<Appointment, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        dateCol.setPrefWidth(110);

        TableColumn<Appointment, LocalTime> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        timeCol.setPrefWidth(90);

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, patCol, docCol, dateCol, timeCol, statusCol);
        table.setItems(data);

        Scene scene = new Scene(table, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}
