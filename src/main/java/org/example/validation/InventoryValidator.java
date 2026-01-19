package org.example.validation;

import org.example.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class InventoryValidator {

    public List<String> validate(String name, String quantity, String unit) {
        List<String> errors = new ArrayList<>();

        if (!ValidationUtils.isValidInventoryName(name)) {
            errors.add(ValidationUtils.getInventoryNameErrorMessage());
        }

        if (!ValidationUtils.isValidQuantity(quantity)) {
            errors.add(ValidationUtils.getQuantityErrorMessage());
        }

        if (unit == null || unit.isEmpty()) {
            errors.add("Unit is required.");
        } else if (!ValidationUtils.isValidUnit(unit)) {
            errors.add(ValidationUtils.getUnitErrorMessage());
        }

        return errors;
    }
}
