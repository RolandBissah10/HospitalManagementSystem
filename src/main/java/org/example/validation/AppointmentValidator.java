package org.example.validation;

import org.example.model.Patient;
import org.example.model.Doctor;
import org.example.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class AppointmentValidator {

    public List<String> validate(Object patient, Object doctor, String date, String time, String reason) {
        List<String> errors = new ArrayList<>();

        if (patient == null) {
            errors.add("Please select a patient.");
        }

        if (doctor == null) {
            errors.add("Please select a doctor.");
        }

        if (date == null || date.isEmpty()) {
            errors.add("Please select a date.");
        } else if (!ValidationUtils.isValidDate(date)) {
            errors.add(ValidationUtils.getDateErrorMessage());
        }

        if (!ValidationUtils.isValidTime(time)) {
            errors.add(ValidationUtils.getTimeErrorMessage());
        }

        if (reason == null || reason.trim().isEmpty()) {
            errors.add("Reason is required.");
        }

        return errors;
    }
}
