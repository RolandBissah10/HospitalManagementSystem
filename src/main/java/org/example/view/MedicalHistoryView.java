package org.example.view;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.dao.MedicalLogDAO;
import org.example.model.MedicalLog;
import org.example.model.Patient;
import org.example.model.Prescription;
import org.example.service.PrescriptionService;

import java.sql.SQLException;

public class MedicalHistoryView {

    public void show(Patient patient, MedicalLogDAO medicalLogDAO, PrescriptionService prescriptionService) {
        Stage historyStage = new Stage();
        historyStage.setTitle("Medical History - " + patient.getFirstName() + " " + patient.getLastName());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Medical Logs (MongoDB)
        Tab logsTab = new Tab("Medical Logs");
        TableView<MedicalLog> logsTable = new TableView<>();

        TableColumn<MedicalLog, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTimestamp().toString()));

        TableColumn<MedicalLog, String> severityCol = new TableColumn<>("Severity");
        severityCol.setCellValueFactory(new PropertyValueFactory<>("severity"));

        TableColumn<MedicalLog, String> contentCol = new TableColumn<>("Content");
        contentCol.setCellValueFactory(new PropertyValueFactory<>("logContent"));

        logsTable.getColumns().addAll(dateCol, severityCol, contentCol);

        // Fetch logs
        try {
            logsTable.setItems(FXCollections.observableArrayList(medicalLogDAO.getLogsByPatientId(patient.getId())));
        } catch (Exception e) {
            logsTable.setPlaceholder(new Label("Error fetching logs: " + e.getMessage()));
        }

        logsTab.setContent(logsTable);

        // Tab 2: Prescriptions (MySQL)
        Tab prescriptionsTab = new Tab("Prescriptions");
        TableView<Prescription> prescriptionsTable = new TableView<>();

        TableColumn<Prescription, String> pDateCol = new TableColumn<>("Date");
        pDateCol.setCellValueFactory(new PropertyValueFactory<>("prescriptionDate"));

        TableColumn<Prescription, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        TableColumn<Prescription, String> diagnosisCol = new TableColumn<>("Diagnosis");
        diagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        TableColumn<Prescription, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        prescriptionsTable.getColumns().addAll(pDateCol, doctorCol, diagnosisCol, notesCol);

        // Fetch prescriptions
        try {
            prescriptionsTable.setItems(FXCollections.observableArrayList(
                    prescriptionService.getPrescriptionsByPatient(patient.getId())));
        } catch (SQLException e) {
            prescriptionsTable.setPlaceholder(new Label("Error: " + e.getMessage()));
        }

        prescriptionsTab.setContent(prescriptionsTable);

        tabPane.getTabs().addAll(logsTab, prescriptionsTab);

        Scene scene = new Scene(tabPane, 800, 600);
        historyStage.setScene(scene);
        historyStage.show();
    }
}
