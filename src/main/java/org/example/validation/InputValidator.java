package org.example.validation;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

import java.util.function.Predicate;

public class InputValidator {

    /**
     * Binds a TextInputControl (TextField, TextArea) to a validation rule.
     * Updates the errorLabel automatically when text changes.
     *
     * @param field        The input field to monitor.
     * @param rule         The predicate to test the input against.
     * @param errorLabel   The label to display the error message on.
     * @param errorMessage The message to display if validation fails.
     */
    public static void bind(TextInputControl field, Predicate<String> rule, Label errorLabel, String errorMessage) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !rule.test(newVal)) {
                errorLabel.setText(errorMessage);
            } else {
                errorLabel.setText("");
            }
        });
    }

    /**
     * Binds a TextInputControl to a validation rule, ignoring empty input (optional
     * fields).
     *
     * @param field        The input field to monitor.
     * @param rule         The predicate to test the input against.
     * @param errorLabel   The label to display the error message on.
     * @param errorMessage The message to display if validation fails.
     */
    public static void bindOptional(TextInputControl field, Predicate<String> rule, Label errorLabel,
            String errorMessage) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty() && !rule.test(newVal)) {
                errorLabel.setText(errorMessage);
            } else {
                errorLabel.setText("");
            }
        });
    }
}
