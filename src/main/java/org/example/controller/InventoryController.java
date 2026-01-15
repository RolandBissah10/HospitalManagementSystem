package org.example.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.dao.MedicalInventoryDAO;
import org.example.model.MedicalInventory;
import org.example.service.HospitalService;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class InventoryController {
    private final MedicalInventoryDAO inventoryDAO = new MedicalInventoryDAO();
    private final HospitalService hospitalService = new HospitalService();

    public void viewInventory() {
        try {
            Stage stage = new Stage();
            TableView<MedicalInventory> table = new TableView<>(
                    FXCollections.observableArrayList(inventoryDAO.getAllInventory()));

            TableColumn<MedicalInventory, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            idCol.setPrefWidth(50);

            TableColumn<MedicalInventory, String> nameCol = new TableColumn<>("Item");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

            TableColumn<MedicalInventory, Integer> qtyCol = new TableColumn<>("Qty");
            qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

            TableColumn<MedicalInventory, String> unitCol = new TableColumn<>("Unit");
            unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));

            table.getColumns().addAll(idCol, nameCol, qtyCol, unitCol);
            stage.setScene(new Scene(table, 400, 400));
            stage.setTitle("Inventory");
            stage.show();
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void addInventoryItem() {
        Dialog<MedicalInventory> dialog = new Dialog<>();
        dialog.setTitle("Add Item");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField name = new TextField();
        name.setPromptText("Name");
        TextField qty = new TextField();
        qty.setPromptText("Qty");
        TextField unit = new TextField();
        unit.setPromptText("Unit");

        grid.addRow(0, new Label("Name:"), name);
        grid.addRow(1, new Label("Qty:"), qty);
        grid.addRow(2, new Label("Unit:"), unit);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                MedicalInventory m = new MedicalInventory();
                m.setItemName(name.getText());
                try {
                    m.setQuantity(Integer.parseInt(qty.getText()));
                } catch (Exception e) {
                    return null;
                }
                m.setUnit(unit.getText());
                return m;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(item -> {
            if (!ValidationUtils.isValidInventoryName(item.getItemName())) {
                AlertUtils.showAlert("Error", "Invalid Name", Alert.AlertType.ERROR);
                return;
            }
            try {
                inventoryDAO.addInventoryItem(item);
                AlertUtils.showAlert("Success", "Item Added", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    public void updateInventoryItem() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Update Item");
        d.setContentText("ID:");
        d.showAndWait().ifPresent(idStr -> {
            try {
                int id = Integer.parseInt(idStr);
                // Implementation simplified: ideally fetch item, populate dialog, save
                // For brevity, using prompt for new qty only
                TextInputDialog q = new TextInputDialog();
                q.setTitle("New Quantity");
                q.showAndWait().ifPresent(qtyStr -> {
                    try {
                        inventoryDAO.updateInventoryQuantity(id, Integer.parseInt(qtyStr));
                        AlertUtils.showAlert("Success", "Updated", Alert.AlertType.INFORMATION);
                    } catch (Exception e) {
                        AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                    }
                });
            } catch (Exception e) {
                AlertUtils.showAlert("Error", "Invalid ID", Alert.AlertType.ERROR);
            }
        });
    }

    public void deleteInventoryItem() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Delete Item");
        d.setContentText("ID:");
        d.showAndWait().ifPresent(id -> {
            try {
                inventoryDAO.deleteInventoryItem(Integer.parseInt(id));
                AlertUtils.showAlert("Success", "Deleted", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    public void checkLowStock() {
        try {
            List<MedicalInventory> low = hospitalService.getLowStockItems();
            if (low.isEmpty())
                AlertUtils.showAlert("Stock", "All good", Alert.AlertType.INFORMATION);
            else {
                StringBuilder sb = new StringBuilder("Low Stock:\n");
                for (MedicalInventory m : low)
                    sb.append(m.getItemName()).append(": ").append(m.getQuantity()).append("\n");
                AlertUtils.showAlert("Warning", sb.toString(), Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
