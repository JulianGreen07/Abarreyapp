package com.abarreyapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    /**
     * Return a fresh Connection for each caller. Using a single shared Connection
     * caused unexpected close/commit interactions when callers used
     * try-with-resources. Creating a new connection per call is safer and
     * consistent with the DAO usage throughout the app.
     */
    public static Connection getConnection() throws SQLException {
        String host = DBConfig.get("db.host", "localhost");
        String port = DBConfig.get("db.port", "3306");
        String database = DBConfig.get("db.database", "abarrey_db");
        String user = DBConfig.get("db.user", "root");
        String pass = DBConfig.get("db.password", "root");
        String params = DBConfig.get("db.params", "useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        String url = String.format("jdbc:mysql://%s:%s/%s?%s", host, port, database, params);
        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * No-op. DAOs use try-with-resources so connections are closed by callers.
     * Kept for backwards compatibility.
     */
    public static void closeConnection() {
        // intentionally empty
    }
}
