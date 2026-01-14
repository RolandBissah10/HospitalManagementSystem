package org.example.controller;

import javafx.fxml.FXML;
import org.example.util.DatabaseUpdater;
import java.sql.SQLException;

public class MainController {
    @FXML
    public void initialize() {
        try {
            DatabaseUpdater.updateSchema();
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
        }
    }
}