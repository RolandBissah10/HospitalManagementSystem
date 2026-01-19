package org.example.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import org.example.dao.MedicalInventoryDAO;
import org.example.model.MedicalInventory;
import org.example.service.HospitalService;
import org.example.util.AlertUtils;
import org.example.view.InventoryFormView;
import org.example.view.InventoryTableView;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class InventoryController {
    private final MedicalInventoryDAO inventoryDAO = new MedicalInventoryDAO();
    private final HospitalService hospitalService = new HospitalService();

    // Views
    private final InventoryFormView inventoryFormView = new InventoryFormView();
    private final InventoryTableView inventoryTableView = new InventoryTableView();

    public void viewInventory() {
        try {
            inventoryTableView.show(inventoryDAO.getAllInventory());
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void addInventoryItem() {
        inventoryFormView.showAddInventoryDialog(inventoryDAO);
    }

    public void updateInventoryItem() {
        inventoryFormView.showUpdateStockDialog(inventoryDAO);
    }

    public void deleteInventoryItem() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Delete Inventory Item");
        d.setHeaderText("Remove item from inventory");
        d.setContentText("Item ID:");
        d.showAndWait().ifPresent(id -> {
            try {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Deletion");
                confirm.setHeaderText("Delete Item ID: " + id);
                confirm.setContentText("Are you sure you want to remove this item from inventory?");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    inventoryDAO.deleteInventoryItem(Integer.parseInt(id));
                    AlertUtils.showAlert("Success", "Item removed from inventory", Alert.AlertType.INFORMATION);
                }
            } catch (NumberFormatException e) {
                AlertUtils.showAlert("Error", "Invalid Item ID", Alert.AlertType.ERROR);
            } catch (Exception e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    public void checkLowStock() {
        try {
            List<MedicalInventory> low = hospitalService.getLowStockItems();
            if (low.isEmpty()) {
                AlertUtils.showAlert("Stock Status", "All medical supplies are adequately stocked",
                        Alert.AlertType.INFORMATION);
            } else {
                inventoryFormView.showLowStockAlert(low);
            }
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
