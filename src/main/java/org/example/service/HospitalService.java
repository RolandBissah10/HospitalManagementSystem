package org.example.service;

import org.example.dao.*;
import org.example.model.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HospitalService {
    private final PatientDAO patientDAO = new PatientDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final MedicalInventoryDAO inventoryDAO = new MedicalInventoryDAO();
    private final PatientFeedbackDAO feedbackDAO = new PatientFeedbackDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    private final Map<Integer, Patient> patientCache = new ConcurrentHashMap<>();

    public List<Patient> getAllPatients() throws SQLException {
        if (patientCache.isEmpty()) {
            patientDAO.getAllPatients().forEach(p -> patientCache.put(p.getId(), p));
        }
        return new ArrayList<>(patientCache.values());
    }

    public void addPatient(Patient p) throws SQLException {
        patientDAO.addPatient(p);
        patientCache.put(p.getId(), p);
    }

    public List<Patient> searchAndSortPatients(String term, String field, boolean asc) throws SQLException {
        List<Patient> list = patientDAO.searchPatients(term);
        if (list.size() < 2)
            return list;
        quickSort(list, 0, list.size() - 1, field, asc);
        return list;
    }

    private void quickSort(List<Patient> list, int low, int high, String field, boolean asc) {
        if (low < high) {
            int pi = partition(list, low, high, field, asc);
            quickSort(list, low, pi - 1, field, asc);
            quickSort(list, pi + 1, high, field, asc);
        }
    }

    private int partition(List<Patient> list, int low, int high, String field, boolean asc) {
        Patient pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (compare(list.get(j), pivot, field, asc) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    private int compare(Patient p1, Patient p2, String field, boolean asc) {
        int res = 0;
        switch (field.toLowerCase()) {
            case "name":
                res = (p1.getFirstName() + p1.getLastName()).compareToIgnoreCase(p2.getFirstName() + p2.getLastName());
                break;
            case "id":
                res = Integer.compare(p1.getId(), p2.getId());
                break;
            default:
                res = Integer.compare(p1.getId(), p2.getId());
        }
        return asc ? res : -res;
    }

    public Map<String, Object> getSystemStatistics() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPatients", patientDAO.getTotalPatientCount());
        stats.put("totalDoctors", doctorDAO.getAllDoctors().size());
        stats.put("totalAppointments", appointmentDAO.getAllAppointments().size());
        stats.put("totalInventory", inventoryDAO.getAllInventory().size());
        stats.put("lowStockItems", inventoryDAO.getLowStockItems(10).size());
        return stats;
    }

    public Map<String, Long> getPerformanceMetrics() throws SQLException {
        Map<String, Long> metrics = new HashMap<>();
        long start = System.currentTimeMillis();
        patientDAO.getAllPatients();
        metrics.put("db_query_ms", System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        getAllPatients();
        metrics.put("cache_lookup_ms", System.currentTimeMillis() - start);
        return metrics;
    }

    public void addFeedback(PatientFeedback f) throws SQLException {
        f.setFeedbackDate(LocalDateTime.now());
        feedbackDAO.addFeedback(f);
    }

    public List<PatientFeedback> getAllFeedback() throws SQLException {
        return feedbackDAO.getAllFeedback();
    }

    public List<MedicalInventory> getLowStockItems() throws SQLException {
        return inventoryDAO.getLowStockItems(10);
    }

    // Department Methods
    public List<Department> getAllDepartments() throws SQLException {
        return departmentDAO.getAllDepartments();
    }

    public void addDepartment(Department d) throws SQLException {
        departmentDAO.addDepartment(d);
    }

    public void updateDepartment(Department d) throws SQLException {
        departmentDAO.updateDepartment(d);
    }

    public void deleteDepartment(int id) throws SQLException {
        departmentDAO.deleteDepartment(id);
    }

    public Department getDepartment(int id) throws SQLException {
        return departmentDAO.getDepartment(id);
    }
}