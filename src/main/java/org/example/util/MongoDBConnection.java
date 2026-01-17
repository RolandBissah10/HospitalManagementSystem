package org.example.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    // Connection string with TLS configuration for Java 23 compatibility
    private static final String CONNECTION_STRING = EnvLoader.get("MONGO_URI");
    private static final String DATABASE_NAME = EnvLoader.get("MONGO_DB");

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
