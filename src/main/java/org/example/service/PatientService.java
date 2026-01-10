package org.example.service;

import org.example.dao.PatientDAO;
import org.example.model.Patient;

import java.sql.SQLException;
import java.util.*;

public class PatientService {
    private PatientDAO patientDAO = new PatientDAO();
    private Map<String, Patient> patientCache = new HashMap<>();
    private List<Patient> patientListCache = new ArrayList<>();

    public void addPatient(Patient patient) throws SQLException {
        patientDAO.addPatient(patient);
        invalidateCache();
    }

    public Patient getPatient(String email) throws SQLException {
        if (patientCache.containsKey(email)) {
            return patientCache.get(email);
        }
        Patient patient = patientDAO.getPatient(email);
        if (patient != null) {
            patientCache.put(email, patient);
        }
        return patient;
    }

    public Patient getPatientById(int id) throws SQLException {
        Patient patient = patientDAO.getPatientById(id);
        if (patient != null) {
            patientCache.put(patient.getEmail(), patient);
        }
        return patient;
    }

    public List<Patient> getAllPatients() throws SQLException {
        if (!patientListCache.isEmpty()) {
            return new ArrayList<>(patientListCache);
        }
        patientListCache = patientDAO.getAllPatients();
        for (Patient p : patientListCache) {
            patientCache.put(p.getEmail(), p);
        }
        return new ArrayList<>(patientListCache);
    }

    public void updatePatient(Patient patient, String originalEmail) throws SQLException {
        patientDAO.updatePatient(patient, originalEmail);
        invalidateCache();
    }

    public void deletePatient(int id) throws SQLException {
        patientDAO.deletePatient(id);
        invalidateCache();
    }

    public List<Patient> searchPatients(String name) throws SQLException {
        List<Patient> all = getAllPatients();
        List<Patient> result = new ArrayList<>();
        for (Patient p : all) {
            if ((p.getFirstName() + " " + p.getLastName()).toLowerCase().contains(name.toLowerCase())) {
                result.add(p);
            }
        }
        // Sort by name
        result.sort(Comparator.comparing(Patient::getFirstName).thenComparing(Patient::getLastName));
        return result;
    }

    private void invalidateCache() {
        patientCache.clear();
        patientListCache.clear();
    }
}