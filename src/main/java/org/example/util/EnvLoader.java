package org.example.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EnvLoader {
    private static final Map<String, String> ENV = new HashMap<>();

    static {
        load();
    }

    private static void load() {
        String envPath = ".env";
        if (!Files.exists(Paths.get(envPath))) {
            System.err.println("Warning: .env file not found at " + Paths.get(envPath).toAbsolutePath());
            return;
        }

        try (Stream<String> lines = Files.lines(Paths.get(envPath))) {
            lines.forEach(line -> {
                line = line.trim();
                // Basic parsing: ignores comments, splits by first '='
                if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                    int equalsIndex = line.indexOf('=');
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    // Optional: remove surrounding quotes
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }
                    ENV.put(key, value);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return ENV.get(key);
    }
}
