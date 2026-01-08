package org.example.dao;

import org.example.model.MedicalInventory;
import org.example.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalInventoryDAO {

    public void addInventoryItem(MedicalInventory item) throws SQLException {
        String sql = "INSERT INTO medical_inventory (item_name, quantity, unit) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getItemName());
            stmt.setInt(2, item.getQuantity());
            stmt.setString(3, item.getUnit());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    public List<MedicalInventory> getAllInventory() throws SQLException {
        List<MedicalInventory> inventory = new ArrayList<>();
        String sql = "SELECT * FROM medical_inventory ORDER BY item_name";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                inventory.add(new MedicalInventory(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getString("unit")));
            }
        }
        return inventory;
    }

    public void updateInventoryQuantity(int itemId, int newQuantity) throws SQLException {
        String sql = "UPDATE medical_inventory SET quantity = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        }
    }

    public void updateInventoryItem(MedicalInventory item) throws SQLException {
        String sql = "UPDATE medical_inventory SET item_name = ?, quantity = ?, unit = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getItemName());
            stmt.setInt(2, item.getQuantity());
            stmt.setString(3, item.getUnit());
            stmt.setInt(4, item.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteInventoryItem(int id) throws SQLException {
        String sql = "DELETE FROM medical_inventory WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<MedicalInventory> searchInventory(String searchTerm) throws SQLException {
        List<MedicalInventory> inventory = new ArrayList<>();
        String sql = "SELECT * FROM medical_inventory WHERE item_name LIKE ? ORDER BY item_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                inventory.add(new MedicalInventory(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getString("unit")));
            }
        }
        return inventory;
    }

    public List<MedicalInventory> getLowStockItems(int threshold) throws SQLException {
        List<MedicalInventory> lowStock = new ArrayList<>();
        String sql = "SELECT * FROM medical_inventory WHERE quantity <= ? ORDER BY quantity";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lowStock.add(new MedicalInventory(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getString("unit")));
            }
        }
        return lowStock;
    }
}