package org.example.model;

public class MedicalInventory {
    private int id;
    private String itemName;
    private int quantity;
    private String unit;

    // Constructors
    public MedicalInventory() {}

    public MedicalInventory(int id, String itemName, int quantity, String unit) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public String toString() {
        return itemName + " - " + quantity + " " + unit;
    }
}