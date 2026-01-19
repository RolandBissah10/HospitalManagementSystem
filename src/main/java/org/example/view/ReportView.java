package org.example.view;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.example.model.Patient;
import org.example.util.AlertUtils;

import java.util.List;
import java.util.Map;

public class ReportView {

    public void showPerformanceReport(Map<String, Object> stats, Map<String, Long> perf) {
        StringBuilder sb = new StringBuilder("System Report\n\nStats:\n");
        stats.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
        sb.append("\nPerformance:\n");
        perf.forEach((k, v) -> sb.append(k).append(": ").append(v).append("ms\n"));

        AlertUtils.showAlert("Report", sb.toString(), Alert.AlertType.INFORMATION);
    }

    public void showCacheStatistics(Map<String, Long> metrics) {
        AlertUtils.showAlert("Cache", "Lookup: " + metrics.get("cache_lookup_ms"), Alert.AlertType.INFORMATION);
    }

    public void showCacheCleared() {
        AlertUtils.showAlert("Done", "Cache Cleared", Alert.AlertType.INFORMATION);
    }

    public void showSortingDemo(List<Patient> patients, List<Patient> sortedPatients) {
        if (patients.size() < 2) {
            AlertUtils.showAlert("Demo", "Not enough patients to demonstrate sorting (need 2+)",
                    Alert.AlertType.WARNING);
            return;
        }

        Stage stage = new Stage();
        TextArea area = new TextArea();
        area.setText("Unsorted:\n" + patients + "\n\nSorted (Demo):\n");
        area.appendText(sortedPatients.toString());

        stage.setScene(new Scene(new VBox(area), 400, 400));
        stage.setTitle("Sorting Demo");
        stage.show();
    }

    public void showAbout() {
        StringBuilder info = new StringBuilder();
        info.append("🏥 GENERAL HOSPITAL MANAGEMENT SYSTEM\n\n");
        info.append("Version: 2.0 (Hybrid Database Architecture)\n");
        info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        info.append("📋 SYSTEM FEATURES:\n");
        info.append("• Patient Management & Registration\n");
        info.append("• Doctor & Staff Management\n");
        info.append("• Appointment Scheduling System\n");
        info.append("• Electronic Prescription Management\n");
        info.append("• Medical Inventory Tracking\n");
        info.append("• Patient Feedback Collection\n");
        info.append("• Medical Logs (NoSQL - MongoDB)\n");
        info.append("• Performance Analytics & Reports\n\n");

        info.append("⚡ PERFORMANCE OPTIMIZATIONS:\n");
        info.append("• ConcurrentHashMap caching\n");
        info.append("• Custom QuickSort algorithm\n");
        info.append("• B-Tree database indexing\n");
        info.append("• ~80% search time reduction\n\n");

        info.append("👥 USER ROLES:\n");
        info.append("• Administrator: Full system access\n");
        info.append("• Doctor: Patient care & prescriptions\n");
        info.append("• Receptionist: Registration & scheduling\n");
        info.append("• Patient: View appointments & feedback\n\n");

        info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        info.append("For support: support@generalhospital.com\n");
        info.append("Emergency: 911\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Hospital Management System");
        alert.setHeaderText("General Hospital - Healthcare Excellence");
        alert.setContentText(info.toString());
        alert.getDialogPane().setPrefWidth(600);
        alert.showAndWait();
    }
}
