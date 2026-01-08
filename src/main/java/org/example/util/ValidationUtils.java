package org.example.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtils {

    // Regex patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s\\-']{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-\\(\\)]{10,15}$");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-\\,\\.#]{5,200}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern ID_PATTERN = Pattern.compile("^[1-9]\\d*$"); // Positive integers only
    private static final Pattern DIAGNOSIS_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-\\,\\.\\(\\)]{3,500}$");
    private static final Pattern MEDICATION_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-]{3,100}$");
    private static final Pattern DOSAGE_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)?\\s*[A-Za-z]{1,10}$");
    private static final Pattern SPECIALTY_PATTERN = Pattern.compile("^[A-Za-z\\s\\-]{3,100}$");
    private static final Pattern INVENTORY_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-]{3,100}$");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("^[1-9]\\d*$"); // Positive integers
    private static final Pattern UNIT_PATTERN = Pattern.compile("^[A-Za-z]{2,20}$");

    // Validation methods
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        return email == null || email.isEmpty() || EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone == null || phone.isEmpty() || PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidAddress(String address) {
        return address == null || address.isEmpty() || ADDRESS_PATTERN.matcher(address.trim()).matches();
    }

    public static boolean isValidDate(String date) {
        if (date == null) return false;
        try {
            LocalDate.parse(date);
            return DATE_PATTERN.matcher(date).matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidTime(String time) {
        return time != null && TIME_PATTERN.matcher(time.trim()).matches();
    }

    public static boolean isValidId(String id) {
        return id != null && ID_PATTERN.matcher(id.trim()).matches();
    }

    public static boolean isValidDiagnosis(String diagnosis) {
        return diagnosis == null || diagnosis.isEmpty() || DIAGNOSIS_PATTERN.matcher(diagnosis.trim()).matches();
    }

    public static boolean isValidMedication(String medication) {
        return medication != null && MEDICATION_PATTERN.matcher(medication.trim()).matches();
    }

    public static boolean isValidDosage(String dosage) {
        return dosage != null && DOSAGE_PATTERN.matcher(dosage.trim()).matches();
    }

    public static boolean isValidSpecialty(String specialty) {
        return specialty != null && SPECIALTY_PATTERN.matcher(specialty.trim()).matches();
    }

    public static boolean isValidInventoryName(String name) {
        return name != null && INVENTORY_NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidQuantity(String quantity) {
        return quantity != null && QUANTITY_PATTERN.matcher(quantity.trim()).matches();
    }

    public static boolean isValidUnit(String unit) {
        return unit == null || unit.isEmpty() || UNIT_PATTERN.matcher(unit.trim()).matches();
    }

    // Error messages
    public static String getNameErrorMessage() {
        return "Name must be 2-50 characters, letters, spaces, hyphens, or apostrophes only";
    }

    public static String getEmailErrorMessage() {
        return "Invalid email format. Example: user@example.com";
    }

    public static String getPhoneErrorMessage() {
        return "Phone must be 10-15 digits, may include +, -, (), or spaces";
    }

    public static String getAddressErrorMessage() {
        return "Address must be 5-200 characters, alphanumeric with spaces, commas, dots, #, or hyphens";
    }

    public static String getDateErrorMessage() {
        return "Date must be in YYYY-MM-DD format";
    }

    public static String getTimeErrorMessage() {
        return "Time must be in HH:MM format (24-hour)";
    }

    public static String getIdErrorMessage() {
        return "ID must be a positive number";
    }

    public static String getDiagnosisErrorMessage() {
        return "Diagnosis must be 3-500 characters, alphanumeric with basic punctuation";
    }

    public static String getMedicationErrorMessage() {
        return "Medication name must be 3-100 characters, alphanumeric with spaces or hyphens";
    }

    public static String getDosageErrorMessage() {
        return "Dosage must be like '10mg', '2.5ml', or '500 units'";
    }

    public static String getSpecialtyErrorMessage() {
        return "Specialty must be 3-100 characters, letters, spaces, or hyphens";
    }

    public static String getInventoryNameErrorMessage() {
        return "Item name must be 3-100 characters, alphanumeric with spaces or hyphens";
    }

    public static String getQuantityErrorMessage() {
        return "Quantity must be a positive number";
    }

    public static String getUnitErrorMessage() {
        return "Unit must be 2-20 letters only";
    }
}