package com.abarreyapp.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = DBConfig.class.getResourceAsStream("/db.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            System.err.println("Could not load db.properties: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
