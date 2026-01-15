package org.example.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    // Connection string with TLS configuration for Java 23 compatibility
    private static final String CONNECTION_STRING = "mongodb+srv://rolandbissah10_db_user:7uBXvdN4cwXhuc2O@cluster0.8egkscw.mongodb.net/?retryWrites=true&w=majority&tls=true&tlsAllowInvalidHostnames=true";
    private static final String DATABASE_NAME = "hospital_logs_db";

    private static MongoClient mongoClient = null;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            try {
                mongoClient = MongoClients.create(CONNECTION_STRING);
            } catch (Exception e) {
                System.err.println("Failed to connect to MongoDB: " + e.getMessage());
                throw new RuntimeException(
                        "MongoDB Connection failed. Please check CONNECTION_STRING in MongoDBConnection.java");
            }
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}
