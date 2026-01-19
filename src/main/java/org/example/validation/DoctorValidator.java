package org.example.validation;

import org.example.service.DoctorService;
import org.example.util.ValidationUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorValidator {

    public List<String> validate(String firstName, String lastName, String specialty, Object department, String phone,
            String email, DoctorService service) {
        List<String> errors = new ArrayList<>();

        if (!ValidationUtils.isValidName(firstName)) {
            errors.add(ValidationUtils.getNameErrorMessage());
        }

        if (!ValidationUtils.isValidName(lastName)) {
            errors.add(ValidationUtils.getNameErrorMessage());
        }

        if (!ValidationUtils.isValidSpecialty(specialty)) {
            errors.add(ValidationUtils.getSpecialtyErrorMessage());
        }

        if (department == null) {
            errors.add("Please select a department.");
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            errors.add(ValidationUtils.getPhoneErrorMessage());
        }

        if (email != null && !email.isEmpty()) {
            if (!ValidationUtils.isValidEmail(email)) {
                errors.add(ValidationUtils.getEmailErrorMessage());
            } else if (service != null) {
                try {
                    if (service.getDoctor(email) != null) {
                        errors.add("Email already exists.");
                    }
                } catch (SQLException e) {
                    errors.add("Database error checking email: " + e.getMessage());
                }
            }
        } else {
            errors.add(ValidationUtils.getEmailErrorMessage());
        }

        return errors;
    }
}
