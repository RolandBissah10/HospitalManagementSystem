package org.example.service;

import org.example.dao.PrescriptionDAO;
import org.example.model.Prescription;
import org.example.model.PrescriptionItem;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PrescriptionService {
    private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    // Caching structures
    private Map<Integer, Prescription> prescriptionCache = new ConcurrentHashMap<>();
    private Map<Integer, List<PrescriptionItem>> prescriptionItemsCache = new ConcurrentHashMap<>();

    public void addPrescription(Prescription prescription, List<PrescriptionItem> items) throws SQLException {
        prescriptionDAO.addPrescriptionWithItems(prescription, items);
        invalidateCache();
    }

    public void updatePrescription(Prescription prescription, List<PrescriptionItem> items) throws SQLException {
        prescriptionDAO.updatePrescriptionWithItems(prescription, items);
        invalidateCache();
    }

    public Prescription getPrescription(int id) throws SQLException {
        if (prescriptionCache.containsKey(id)) {
            return prescriptionCache.get(id);
        }

        Prescription prescription = prescriptionDAO.getPrescription(id);
        if (prescription != null) {
            prescriptionCache.put(id, prescription);
        }
        return prescription;
    }

    public List<Prescription> getPrescriptionsByPatient(int patientId) throws SQLException {
        return prescriptionDAO.getPrescriptionsByPatient(patientId);
    }

    public List<Prescription> getPrescriptionsByDoctor(int doctorId) throws SQLException {
        return prescriptionDAO.getPrescriptionsByDoctor(doctorId);
    }

    public List<PrescriptionItem> getPrescriptionItems(int prescriptionId) throws SQLException {
        if (prescriptionItemsCache.containsKey(prescriptionId)) {
            return new ArrayList<>(prescriptionItemsCache.get(prescriptionId));
        }

        List<PrescriptionItem> items = prescriptionDAO.getPrescriptionItems(prescriptionId);
        prescriptionItemsCache.put(prescriptionId, new ArrayList<>(items));
        return new ArrayList<>(items);
    }

    public List<Prescription> getAllPrescriptions() throws SQLException {
        List<Prescription> prescriptions = prescriptionDAO.getAllPrescriptions();

        // Update cache
        for (Prescription p : prescriptions) {
            prescriptionCache.put(p.getId(), p);
        }

        return prescriptions;
    }

    public void deletePrescription(int id) throws SQLException {
        prescriptionDAO.deletePrescription(id);
        invalidateCache();
    }

    public Map<String, Object> getPrescriptionStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        List<Prescription> allPrescriptions = getAllPrescriptions();

        stats.put("totalPrescriptions", allPrescriptions.size());

        // Count by month
        Map<String, Integer> prescriptionsByMonth = new TreeMap<>();
        for (Prescription p : allPrescriptions) {
            String monthKey = p.getPrescriptionDate().getMonth().toString() + " " +
                    p.getPrescriptionDate().getYear();
            prescriptionsByMonth.put(monthKey, prescriptionsByMonth.getOrDefault(monthKey, 0) + 1);
        }
        stats.put("prescriptionsByMonth", prescriptionsByMonth);

        // Cache statistics
        stats.put("prescriptionCacheSize", prescriptionCache.size());
        stats.put("prescriptionItemsCacheSize", prescriptionItemsCache.size());

        return stats;
    }

    private void invalidateCache() {
        prescriptionCache.clear();
        prescriptionItemsCache.clear();
    }

    public void clearCache() {
        invalidateCache();
    }
}