package org.example.validation;

import org.example.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionValidator {

    public List<String> validate(Object patient, Object doctor, String medication, String dosage, String duration) {
        List<String> errors = new ArrayList<>();

        if (patient == null) {
            errors.add("Please select a patient.");
        }

        if (doctor == null) {
            errors.add("Please select a doctor.");
        }

        if (!ValidationUtils.isValidMedication(medication)) {
            errors.add(ValidationUtils.getMedicationErrorMessage());
        }

        if (!ValidationUtils.isValidDosage(dosage)) {
            errors.add(ValidationUtils.getDosageErrorMessage());
        }

        if (duration == null || duration.trim().isEmpty()) {
            errors.add("Duration is required.");
        } else {
            try {
                int days = Integer.parseInt(duration);
                if (days <= 0) {
                    errors.add("Duration must be a positive number of days.");
                }
            } catch (NumberFormatException e) {
                errors.add("Duration must be a valid number.");
            }
        }

        return errors;
    }
}
