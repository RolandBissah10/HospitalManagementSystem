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

            TableColumn<MedicalInventory, Integer> idCol = new TableColumn<>("Item ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            idCol.setPrefWidth(70);

            TableColumn<MedicalInventory, String> nameCol = new TableColumn<>("Medical Supply / Equipment");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
            nameCol.setPrefWidth(250);

            TableColumn<MedicalInventory, Integer> qtyCol = new TableColumn<>("Stock Quantity");
            qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            qtyCol.setPrefWidth(120);

            TableColumn<MedicalInventory, String> unitCol = new TableColumn<>("Unit of Measure");
            unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
            unitCol.setPrefWidth(150);

            table.getColumns().addAll(idCol, nameCol, qtyCol, unitCol);
            stage.setScene(new Scene(table, 650, 450));
            stage.setTitle("Medical Inventory");
            stage.show();
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void addInventoryItem() {
        Dialog<MedicalInventory> dialog = new Dialog<>();
        dialog.setTitle("Add Medical Supply");
        dialog.setHeaderText("Enter medical supply or equipment details");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField name = new TextField();
        name.setPromptText("e.g., Surgical Gloves, Paracetamol, Syringes");

        TextField qty = new TextField();
        qty.setPromptText("e.g., 100");

        // Dropdown for common hospital units
        ComboBox<String> unitCombo = new ComboBox<>();
        unitCombo.setItems(FXCollections.observableArrayList(
                "Boxes",
                "Bottles",
                "Vials",
                "Tablets",
                "Capsules",
                "Ampoules",
                "Syringes",
                "Packs",
                "Units",
                "Liters",
                "Milliliters (ml)",
                "Grams (g)",
                "Milligrams (mg)",
                "Pieces",
                "Rolls",
                "Pairs",
                "Sets"));
        unitCombo.setPromptText("Select unit");
        unitCombo.setEditable(true); // Allow custom units

        grid.addRow(0, new Label("Supply/Equipment Name*:"), name);
        grid.addRow(1, new Label("Stock Quantity*:"), qty);
        grid.addRow(2, new Label("Unit of Measure*:"), unitCombo);

        Label helpText = new Label("* Required fields");
        helpText.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
        grid.add(helpText, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                if (name.getText().trim().isEmpty() || qty.getText().trim().isEmpty() ||
                        unitCombo.getValue() == null || unitCombo.getValue().trim().isEmpty()) {
                    AlertUtils.showAlert("Validation Error", "Please fill in all required fields",
                            Alert.AlertType.ERROR);
                    return null;
                }

                MedicalInventory m = new MedicalInventory();
                m.setItemName(name.getText().trim());
                try {
                    int quantity = Integer.parseInt(qty.getText().trim());
                    if (quantity < 0) {
                        AlertUtils.showAlert("Validation Error", "Quantity must be a positive number",
                                Alert.AlertType.ERROR);
                        return null;
                    }
                    m.setQuantity(quantity);
                } catch (NumberFormatException e) {
                    AlertUtils.showAlert("Validation Error", "Quantity must be a valid number", Alert.AlertType.ERROR);
                    return null;
                }
                m.setUnit(unitCombo.getValue().trim());
                return m;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(item -> {
            if (!ValidationUtils.isValidInventoryName(item.getItemName())) {
                AlertUtils.showAlert("Error", "Invalid supply name", Alert.AlertType.ERROR);
                return;
            }
            try {
                inventoryDAO.addInventoryItem(item);
                AlertUtils.showAlert("Success", "Medical supply added to inventory", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    public void updateInventoryItem() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Update Inventory");
        d.setHeaderText("Update stock quantity");
        d.setContentText("Item ID:");
        d.showAndWait().ifPresent(idStr -> {
            try {
                int id = Integer.parseInt(idStr);

                TextInputDialog q = new TextInputDialog();
                q.setTitle("Update Stock Quantity");
                q.setHeaderText("Enter new stock quantity for Item ID: " + id);
                q.setContentText("New Quantity:");
                q.showAndWait().ifPresent(qtyStr -> {
                    try {
                        int newQty = Integer.parseInt(qtyStr);
                        if (newQty < 0) {
                            AlertUtils.showAlert("Error", "Quantity must be positive", Alert.AlertType.ERROR);
                            return;
                        }
                        inventoryDAO.updateInventoryQuantity(id, newQty);
                        AlertUtils.showAlert("Success", "Stock quantity updated successfully",
                                Alert.AlertType.INFORMATION);
                    } catch (NumberFormatException e) {
                        AlertUtils.showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                    } catch (Exception e) {
                        AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                    }
                });
            } catch (NumberFormatException e) {
                AlertUtils.showAlert("Error", "Invalid Item ID", Alert.AlertType.ERROR);
            }
        });
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
                StringBuilder sb = new StringBuilder("⚠️ LOW STOCK ALERT\n\n");
                sb.append("The following items need restocking:\n\n");
                for (MedicalInventory m : low) {
                    sb.append("• ").append(m.getItemName())
                            .append(": ").append(m.getQuantity())
                            .append(" ").append(m.getUnit())
                            .append("\n");
                }
                AlertUtils.showAlert("Low Stock Warning", sb.toString(), Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
