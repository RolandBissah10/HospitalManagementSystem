package org.example.service;

import org.example.dao.DoctorDAO;
import org.example.model.Doctor;

import java.sql.SQLException;
import java.util.*;

public class DoctorService {
    private DoctorDAO doctorDAO = new DoctorDAO();
    private Map<String, Doctor> doctorCache = new HashMap<>();

    public void addDoctor(Doctor doctor) throws SQLException {
        doctorDAO.addDoctor(doctor);
        invalidateCache();
    }

    public Doctor getDoctor(String email) throws SQLException {
        if (doctorCache.containsKey(email)) {
            return doctorCache.get(email);
        }
        Doctor doctor = doctorDAO.getDoctor(email);
        if (doctor != null) {
            doctorCache.put(email, doctor);
        }
        return doctor;
    }

    public Doctor getDoctorById(int id) throws SQLException {
        Doctor doctor = doctorDAO.getDoctorById(id);
        if (doctor != null) {
            doctorCache.put(doctor.getEmail(), doctor);
        }
        return doctor;
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor d : doctors) {
            doctorCache.put(d.getEmail(), d);
        }
        return doctors;
    }

    public void updateDoctor(Doctor doctor, String originalEmail) throws SQLException {
        doctorDAO.updateDoctor(doctor, originalEmail);
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