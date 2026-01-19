package org.example.validation;

import java.util.ArrayList;
import java.util.List;

public class FeedbackValidator {

    public List<String> validate(String ratingStr, String comments) {
        List<String> errors = new ArrayList<>();

        int rating = 0;
        try {
            rating = Integer.parseInt(ratingStr);
            if (rating < 1 || rating > 5) {
                errors.add("Rating must be between 1 and 5.");
            }
        } catch (NumberFormatException e) {
            errors.add("Rating must be a valid number.");
        }

        if (comments != null && comments.length() > 500) {
            errors.add("Comments cannot exceed 500 characters.");
        }

        return errors;
    }
}
