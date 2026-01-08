module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example to javafx.fxml;
    opens org.example.controller to javafx.fxml;
    exports org.example;
    exports org.example.model;
    exports org.example.service;
    exports org.example.dao;
    exports org.example.util;
    exports org.example.controller;
}