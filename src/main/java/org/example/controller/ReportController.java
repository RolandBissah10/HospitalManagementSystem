package org.example.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Patient;
import org.example.service.HospitalService;
import org.example.service.PatientService;
import org.example.util.AlertUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ReportController {
    private final HospitalService hospitalService = new HospitalService();
    private final PatientService patientService = new PatientService();

    public void generatePerformanceReport() {
        try {
            Map<String, Object> stats = hospitalService.getSystemStatistics();
            Map<String, Long> perf = hospitalService.getPerformanceMetrics();

            StringBuilder sb = new StringBuilder("System Report\n\nStats:\n");
            stats.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
            sb.append("\nPerformance:\n");
            perf.forEach((k, v) -> sb.append(k).append(": ").append(v).append("ms\n"));

            AlertUtils.showAlert("Report", sb.toString(), Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void showCacheStatistics() {
        try {
            Map<String, Long> m = hospitalService.getPerformanceMetrics();
            AlertUtils.showAlert("Cache", "Lookup: " + m.get("cache_lookup_ms"), Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void clearCache() {
        // Mock impl
        AlertUtils.showAlert("Done", "Cache Cleared", Alert.AlertType.INFORMATION);
    }

    public void showSortingDemo() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            if (patients.size() < 2) {
                AlertUtils.showAlert("Demo", "Not enough patients", Alert.AlertType.WARNING);
                return;
            }
            // Simplified demo
            Stage stage = new Stage();
            TextArea area = new TextArea();
            area.setText("Unsorted:\n" + patients + "\n\nSorted (Demo):\n");

            List<Patient> sorted = hospitalService.searchAndSortPatients("", "name", true);
            area.appendText(sorted.toString());

            stage.setScene(new Scene(new VBox(area), 400, 400));
            stage.setTitle("Sorting Demo");
            stage.show();
        } catch (Exception e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void showAbout() {
        AlertUtils.showAlert("About", "Hospital Management System v2.0\nMicroservices Architecture",
                Alert.AlertType.INFORMATION);
    }
}
