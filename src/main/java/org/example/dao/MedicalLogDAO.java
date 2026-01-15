package org.example.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.example.model.MedicalLog;
import org.example.util.MongoDBConnection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicalLogDAO {

    private MongoCollection<Document> getCollection() {
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            return db.getCollection("medical_logs");
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle gracefully in service/controller
        }
    }

    public void addLog(MedicalLog log) {
        MongoCollection<Document> col = getCollection();
        if (col != null) {
            col.insertOne(log.toDocument());
        }
    }

    public List<MedicalLog> getLogsByPatientId(int patientId) {
        List<MedicalLog> logs = new ArrayList<>();
        MongoCollection<Document> col = getCollection();
        if (col != null) {
            for (Document doc : col.find(Filters.eq("patient_id", patientId))) {
                logs.add(MedicalLog.fromDocument(doc));
            }
        }
        return logs;
    }

    public List<MedicalLog> getAllLogs() {
        List<MedicalLog> logs = new ArrayList<>();
        MongoCollection<Document> col = getCollection();
        if (col != null) {
            for (Document doc : col.find()) {
                logs.add(MedicalLog.fromDocument(doc));
            }
        }
        return logs;
    }
}
