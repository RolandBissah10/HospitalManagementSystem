package org.example.service;

import org.example.dao.AppointmentDAO;
import org.example.model.Appointment;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AppointmentService {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private List<Appointment> appointmentCache = new ArrayList<>();
    private Map<Integer, Appointment> appointmentMapCache = new ConcurrentHashMap<>();
    private Map<String, List<Appointment>> appointmentDateCache = new ConcurrentHashMap<>();
    private int cacheHits = 0;
    private int cacheMisses = 0;

    public void addAppointment(Appointment appointment) throws SQLException {
        appointmentDAO.addAppointment(appointment);
        invalidateCache();
    }

    public Appointment getAppointment(int id) throws SQLException {
        if (appointmentMapCache.containsKey(id)) {
            cacheHits++;
            return appointmentMapCache.get(id);
        }
        cacheMisses++;
        Appointment appointment = appointmentDAO.getAppointment(id);
        if (appointment != null) {
            appointmentMapCache.put(id, appointment);
        }
        return appointment;
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        if (!appointmentCache.isEmpty()) {
            cacheHits++;
            return new ArrayList<>(appointmentCache);
        }
        cacheMisses++;
        appointmentCache = appointmentDAO.getAllAppointments();
        for (Appointment appt : appointmentCache) {
            appointmentMapCache.put(appt.getId(), appt);
        }
        return new ArrayList<>(appointmentCache);
    }

    public void updateAppointment(Appointment appointment) throws SQLException {
        appointmentDAO.updateAppointment(appointment);
        invalidateCache();
    }

    public void deleteAppointment(int id) throws SQLException {
        appointmentDAO.deleteAppointment(id);
        invalidateCache();
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) throws SQLException {
        List<Appointment> allAppointments = getAllAppointments();
        List<Appointment> result = new ArrayList<>();

        for (Appointment appt : allAppointments) {
            if (appt.getPatientId() == patientId) {
                result.add(appt);
            }
        }
        return result;
    }

    public List<Appointment> getAppointmentsByDoctorId(int doctorId) throws SQLException {
        List<Appointment> allAppointments = getAllAppointments();
        List<Appointment> result = new ArrayList<>();

        for (Appointment appt : allAppointments) {
            if (appt.getDoctorId() == doctorId) {
                result.add(appt);
            }
        }
        return result;
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) throws SQLException {
        String dateKey = date.toString();
        if (appointmentDateCache.containsKey(dateKey)) {
            cacheHits++;
            return new ArrayList<>(appointmentDateCache.get(dateKey));
        }

        cacheMisses++;
        List<Appointment> allAppointments = getAllAppointments();
        List<Appointment> result = new ArrayList<>();

        for (Appointment appt : allAppointments) {
            if (appt.getAppointmentDate().equals(date)) {
                result.add(appt);
            }
        }

        appointmentDateCache.put(dateKey, new ArrayList<>(result));
        return result;
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        return appointmentDAO.getAppointmentsByDateRange(startDate, endDate);
    }

    public Map<String, Integer> getAppointmentStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        List<Appointment> appointments = getAllAppointments();

        stats.put("total", appointments.size());
        stats.put("scheduled", 0);
        stats.put("completed", 0);
        stats.put("cancelled", 0);

        for (Appointment appt : appointments) {
            String status = appt.getStatus();
            stats.put(status, stats.getOrDefault(status, 0) + 1);
        }

        return stats;
    }

    public double getCacheHitRate() {
        int totalAccesses = cacheHits + cacheMisses;
        return totalAccesses > 0 ? (double) cacheHits / totalAccesses * 100 : 0;
    }

    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheHits", cacheHits);
        stats.put("cacheMisses", cacheMisses);
        stats.put("cacheHitRate", String.format("%.2f%%", getCacheHitRate()));
        stats.put("cacheSize", appointmentMapCache.size());
        stats.put("listCacheSize", appointmentCache.size());
        stats.put("dateCacheSize", appointmentDateCache.size());
        stats.put("avgQueryTime", appointmentDAO.getAverageQueryTime());
        return stats;
    }

    public void scheduleAppointment(Appointment appointment) throws SQLException {
        addAppointment(appointment);
    }

    private void invalidateCache() {
        appointmentCache.clear();
        appointmentMapCache.clear();
        appointmentDateCache.clear();
    }

    public void clearCache() {
        invalidateCache();
        cacheHits = 0;
        cacheMisses = 0;
        appointmentDAO.resetPerformanceStats();
    }
}