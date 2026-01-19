package org.example.validation;

import org.example.service.PatientService;
import org.example.util.ValidationUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientValidator {

    public List<String> validate(String firstName, String lastName, String phone, String email, String address,
            PatientService service) {
        List<String> errors = new ArrayList<>();

        if (!ValidationUtils.isValidName(firstName)) {
            errors.add(ValidationUtils.getNameErrorMessage());
        }

        if (!ValidationUtils.isValidName(lastName)) {
            errors.add(ValidationUtils.getNameErrorMessage());
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            errors.add(ValidationUtils.getPhoneErrorMessage());
        }

        if (email != null && !email.isEmpty()) {
            if (!ValidationUtils.isValidEmail(email)) {
                errors.add(ValidationUtils.getEmailErrorMessage());
            } else if (service != null) {
                try {
                    // Check for existing email if service is provided (for new patients)
                    if (service.getPatient(email) != null) {
                        errors.add("Email already exists.");
                    }
                } catch (SQLException e) {
                    errors.add("Database error checking email: " + e.getMessage());
                }
            }
        }

        if (address != null && !address.trim().isEmpty() && !ValidationUtils.isValidAddress(address)) {
            errors.add(ValidationUtils.getAddressErrorMessage());
        }

        return errors;
    }
}
