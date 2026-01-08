package org.example.service;

import org.example.dao.DoctorDAO;
import org.example.model.Doctor;
import org.example.model.Patient;

import java.sql.SQLException;
import java.util.*;

public class DoctorService {
    private DoctorDAO doctorDAO = new DoctorDAO();
    private Map<Integer, Doctor> doctorCache = new HashMap<>();

    public void addDoctor(Doctor doctor) throws SQLException {
        doctorDAO.addDoctor(doctor);
        invalidateCache();
    }

    public Doctor getDoctor(int id) throws SQLException {
        if (doctorCache.containsKey(id)) {
            return doctorCache.get(id);
        }
        Doctor doctor = doctorDAO.getDoctor(id);
        if (doctor != null) {
            doctorCache.put(id, doctor);
        }
        return doctor;
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        // Populate cache
        for (Doctor d : doctors) {
            doctorCache.put(d.getId(), d);
        }
        return doctors;
    }

    public void updateDoctor(Doctor doctor) throws SQLException {
        doctorDAO.updateDoctor(doctor);
        invalidateCache();
    }

    public void deleteDoctor(int id) throws SQLException {
        doctorDAO.deleteDoctor(id);
        invalidateCache();
    }

    private void invalidateCache() {
        doctorCache.clear();
    }
}