package org.example.controller;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import org.example.model.Department;
import org.example.service.HospitalService;
import org.example.util.AlertUtils;
import org.example.view.DepartmentView;

import java.sql.SQLException;

public class DepartmentController {
    private final HospitalService hospitalService = new HospitalService();
    private final DepartmentView departmentView = new DepartmentView();

    public void viewDepartments() {
        try {
            departmentView.show(
                    hospitalService.getAllDepartments(),
                    this::addDepartment,
                    this::deleteDepartment);
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void addDepartment(String name) {
        try {
            hospitalService.addDepartment(new Department(0, name));
            AlertUtils.showAlert("Success", "Added", Alert.AlertType.INFORMATION);
            // Ideally refresh view if needed
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
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
