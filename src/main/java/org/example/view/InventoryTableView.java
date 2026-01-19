package org.example.view;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.MedicalInventory;

import java.util.List;

public class InventoryTableView {

    public void show(List<MedicalInventory> inventoryList) {
        Stage stage = new Stage();
        TableView<MedicalInventory> table = new TableView<>(
                FXCollections.observableArrayList(inventoryList));

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
    }
}
