package org.example.controller;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import org.example.model.Patient;
import org.example.service.HospitalService;
import org.example.service.PatientService;
import org.example.util.AlertUtils;
import org.example.view.ReportView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ReportController {
    private final HospitalService hospitalService = new HospitalService();
    private final PatientService patientService = new PatientService();
    private final ReportView reportView = new ReportView();

    public void generatePerformanceReport() {
        try {
            Map<String, Object> stats = hospitalService.getSystemStatistics();
            Map<String, Long> perf = hospitalService.getPerformanceMetrics();
            reportView.showPerformanceReport(stats, perf);
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void showCacheStatistics() {
        try {
            Map<String, Long> m = hospitalService.getPerformanceMetrics();
            reportView.showCacheStatistics(m);
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void clearCache() {
        // Mock impl
        reportView.showCacheCleared();
    }

    public void showSortingDemo() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            // View handles "not enough patients" check/alert
            List<Patient> sorted = hospitalService.searchAndSortPatients("", "name", true);
            reportView.showSortingDemo(patients, sorted);
        } catch (Exception e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void showAbout() {
        reportView.showAbout();
    }
}
