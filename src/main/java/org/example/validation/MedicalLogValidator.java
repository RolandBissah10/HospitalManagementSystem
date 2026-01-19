package org.example.validation;

import java.util.ArrayList;
import java.util.List;

public class MedicalLogValidator {

    public List<String> validate(String content, String severity) {
        List<String> errors = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) {
            errors.add("Log content cannot be empty.");
        }

        if (severity == null || severity.trim().isEmpty()) {
            errors.add("Severity is required.");
        } else if (!severity.equals("Routine") && !severity.equals("Observation") && !severity.equals("Critical")) {
            errors.add("Invalid severity level.");
        }

        return errors;
    }
}
