package org.example.service;

import org.example.dao.*;
import org.example.model.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HospitalService {
    private PatientDAO patientDAO = new PatientDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private MedicalInventoryDAO inventoryDAO = new MedicalInventoryDAO();

    // Advanced caching with sorting indexes
    private Map<Integer, Patient> patientCache = new ConcurrentHashMap<>();
    private Map<String, TreeMap<String, List<Integer>>> patientSortIndexes = new ConcurrentHashMap<>();

    public PerformanceReport generatePerformanceReport() throws SQLException {
        PerformanceReport report = new PerformanceReport();

        // Database query performance
        long startTime = System.currentTimeMillis();
        List<Patient> patients = patientDAO.getAllPatients();
        long endTime = System.currentTimeMillis();
        report.setPatientQueryTime(endTime - startTime);

        startTime = System.currentTimeMillis();
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        endTime = System.currentTimeMillis();
        report.setAppointmentQueryTime(endTime - startTime);

        startTime = System.currentTimeMillis();
        int scheduledCount = appointmentDAO.getAppointmentCountByStatus("scheduled");
        endTime = System.currentTimeMillis();
        report.setComplexQueryTime(endTime - startTime);

        // Cache performance
        report.setCacheStats(getCacheStatistics());

        return report;
    }

    public List<Patient> searchAndSortPatients(String searchTerm, String sortField, boolean ascending) throws SQLException {
        List<Patient> patients = patientDAO.searchPatients(searchTerm);

        // Sort using appropriate algorithm based on data size
        if (patients.size() > 1000) {
            // Use merge sort for large datasets
            patients = mergeSortPatients(patients, sortField, ascending);
        } else if (patients.size() > 100) {
            // Use quick sort for medium datasets
            patients = quickSortPatients(patients, sortField, ascending);
        } else {
            // Use bubble sort for small datasets (for demonstration)
            patients = bubbleSortPatients(patients, sortField, ascending);
        }

        return patients;
    }

    private List<Patient> quickSortPatients(List<Patient> patients, String sortField, boolean ascending) {
        List<Patient> sorted = new ArrayList<>(patients);
        quickSort(sorted, 0, sorted.size() - 1, sortField, ascending);
        return sorted;
    }

    private void quickSort(List<Patient> patients, int low, int high, String sortField, boolean ascending) {
        if (low < high) {
            int pi = partition(patients, low, high, sortField, ascending);
            quickSort(patients, low, pi - 1, sortField, ascending);
            quickSort(patients, pi + 1, high, sortField, ascending);
        }
    }

    private int partition(List<Patient> patients, int low, int high, String sortField, boolean ascending) {
        Patient pivot = patients.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            int comparison = comparePatients(patients.get(j), pivot, sortField);
            if (ascending ? comparison < 0 : comparison > 0) {
                i++;
                Collections.swap(patients, i, j);
            }
        }
        Collections.swap(patients, i + 1, high);
        return i + 1;
    }

    private List<Patient> mergeSortPatients(List<Patient> patients, String sortField, boolean ascending) {
        if (patients.size() <= 1) {
            return patients;
        }

        int mid = patients.size() / 2;
        List<Patient> left = mergeSortPatients(new ArrayList<>(patients.subList(0, mid)), sortField, ascending);
        List<Patient> right = mergeSortPatients(new ArrayList<>(patients.subList(mid, patients.size())), sortField, ascending);

        return mergePatients(left, right, sortField, ascending);
    }

    private List<Patient> mergePatients(List<Patient> left, List<Patient> right, String sortField, boolean ascending) {
        List<Patient> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            int comparison = comparePatients(left.get(i), right.get(j), sortField);
            if (ascending ? comparison <= 0 : comparison > 0) {
                merged.add(left.get(i++));
            } else {
                merged.add(right.get(j++));
            }
        }

        while (i < left.size()) {
            merged.add(left.get(i++));
        }

        while (j < right.size()) {
            merged.add(right.get(j++));
        }

        return merged;
    }

    private List<Patient> bubbleSortPatients(List<Patient> patients, String sortField, boolean ascending) {
        List<Patient> sorted = new ArrayList<>(patients);
        int n = sorted.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                int comparison = comparePatients(sorted.get(j), sorted.get(j + 1), sortField);
                if (ascending ? comparison > 0 : comparison < 0) {
                    Collections.swap(sorted, j, j + 1);
                }
            }
        }

        return sorted;
    }

    private int comparePatients(Patient p1, Patient p2, String sortField) {
        switch (sortField.toLowerCase()) {
            case "name":
                int lastNameCompare = p1.getLastName().compareToIgnoreCase(p2.getLastName());
                if (lastNameCompare != 0) return lastNameCompare;
                return p1.getFirstName().compareToIgnoreCase(p2.getFirstName());
            case "dateofbirth":
                return p1.getDateOfBirth().compareTo(p2.getDateOfBirth());
            case "id":
                return Integer.compare(p1.getId(), p2.getId());
            default:
                return Integer.compare(p1.getId(), p2.getId());
        }
    }

    public Map<String, Object> getSystemStatistics() throws SQLException {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalPatients", patientDAO.getTotalPatientCount());
        stats.put("totalDoctors", doctorDAO.getAllDoctors().size());
        stats.put("totalAppointments", appointmentDAO.getAllAppointments().size());
        stats.put("scheduledAppointments", appointmentDAO.getAppointmentCountByStatus("scheduled"));
        stats.put("completedAppointments", appointmentDAO.getAppointmentCountByStatus("completed"));

        List<MedicalInventory> lowStock = inventoryDAO.getLowStockItems(10);
        stats.put("lowStockItems", lowStock.size());
        stats.put("avgPatientQueryTime", patientDAO.getAverageQueryTime());
        stats.put("avgAppointmentQueryTime", appointmentDAO.getAverageQueryTime());

        return stats;
    }

    private Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("patientCacheSize", patientCache.size());
        return stats;
    }

    public static class PerformanceReport {
        private long patientQueryTime;
        private long appointmentQueryTime;
        private long complexQueryTime;
        private Map<String, Object> cacheStats;

        // Getters and setters
        public long getPatientQueryTime() { return patientQueryTime; }
        public void setPatientQueryTime(long time) { this.patientQueryTime = time; }

        public long getAppointmentQueryTime() { return appointmentQueryTime; }
        public void setAppointmentQueryTime(long time) { this.appointmentQueryTime = time; }

        public long getComplexQueryTime() { return complexQueryTime; }
        public void setComplexQueryTime(long time) { this.complexQueryTime = time; }

        public Map<String, Object> getCacheStats() { return cacheStats; }
        public void setCacheStats(Map<String, Object> stats) { this.cacheStats = stats; }

        @Override
        public String toString() {
            return String.format(
                    "Performance Report:\n" +
                            "Patient Query Time: %d ms\n" +
                            "Appointment Query Time: %d ms\n" +
                            "Complex Query Time: %d ms\n" +
                            "Cache Size: %d",
                    patientQueryTime, appointmentQueryTime, complexQueryTime,
                    cacheStats != null ? (int)cacheStats.get("patientCacheSize") : 0
            );
        }
    }
}