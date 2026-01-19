package org.example.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.dao.MedicalInventoryDAO;
import org.example.model.MedicalInventory;
import org.example.util.AlertUtils;
import org.example.util.ValidationUtils;
import org.example.validation.InputValidator;
import org.example.validation.InventoryValidator;

import java.sql.SQLException;
import java.util.List;

public class InventoryFormView {

    private final InventoryValidator inventoryValidator = new InventoryValidator();

    public void showAddInventoryDialog(MedicalInventoryDAO inventoryDAO) {
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

        Label nameError = createErrorLabel();
        Label qtyError = createErrorLabel();
        Label unitError = createErrorLabel();

        grid.addRow(0, new Label("Supply/Equipment Name*:"), name);
        grid.add(nameError, 2, 0);

        grid.addRow(1, new Label("Stock Quantity*:"), qty);
        grid.add(qtyError, 2, 1);

        grid.addRow(2, new Label("Unit of Measure*:"), unitCombo);
        grid.add(unitError, 2, 2);

        Label helpText = new Label("* Required fields");
        helpText.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
        grid.add(helpText, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Bind validation
        InputValidator.bind(name, ValidationUtils::isValidInventoryName, nameError, "Invalid name");
        InputValidator.bind(qty, ValidationUtils::isValidQuantity, qtyError, "Must be a positive integer");
        // Note: unitCombo is a ComboBox, straightforward binding might be tricky if
        // it's not a TextInputControl.
        // But since it's editable, it has a text property? Using editor.
        InputValidator.bind(unitCombo.getEditor(), ValidationUtils::isValidUnit, unitError, "Invalid unit");

        dialog.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                List<String> errors = inventoryValidator.validate(name.getText(), qty.getText(), unitCombo.getValue());

                if (!errors.isEmpty()) {
                    AlertUtils.showAlert("Validation Error", String.join("\n", errors), Alert.AlertType.ERROR);
                    return null;
                }

                MedicalInventory m = new MedicalInventory();
                m.setItemName(name.getText().trim());
                m.setQuantity(Integer.parseInt(qty.getText().trim()));
                m.setUnit(unitCombo.getValue().trim());
                return m;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(item -> {
            try {
                inventoryDAO.addInventoryItem(item);
                AlertUtils.showAlert("Success", "Medical supply added to inventory", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    public void showUpdateStockDialog(MedicalInventoryDAO inventoryDAO) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Update Inventory");
        d.setHeaderText("Update stock quantity");
        d.setContentText("Item ID:");
        d.showAndWait().ifPresent(idStr -> {
            try {
                int id = Integer.parseInt(idStr);
                // Note: We could validate ID existence here if we had service access easily

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

    private Label createErrorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        return label;
    }

    public void showLowStockAlert(List<org.example.model.MedicalInventory> lowStockItems) {
        StringBuilder sb = new StringBuilder("⚠️ LOW STOCK ALERT\n\n");
        sb.append("The following items need restocking:\n\n");
        for (org.example.model.MedicalInventory m : lowStockItems) {
            sb.append("• ").append(m.getItemName())
                    .append(": ").append(m.getQuantity())
                    .append(" ").append(m.getUnit())
                    .append("\n");
        }
        AlertUtils.showAlert("Low Stock Warning", sb.toString(), javafx.scene.control.Alert.AlertType.WARNING);
    }
}
