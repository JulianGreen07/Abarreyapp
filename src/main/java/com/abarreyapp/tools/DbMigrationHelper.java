package com.abarreyapp.tools;

import com.abarreyapp.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DbMigrationHelper {

    public static void main(String[] args) {
        boolean nullify = false;
        boolean createAdmins = false;
        boolean createLlegadas = false;
        for (String a : args) {
            if ("--nullify".equals(a)) nullify = true;
            if ("--create-admins".equals(a)) createAdmins = true;
            if ("--create-llegadas".equals(a)) createLlegadas = true;
        }

        System.out.println("Starting DB migration helper...");

        try (Connection c = DB.getConnection()) {
            // 1) check password_hash column
            String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='password_hash'";
            try (PreparedStatement ps = c.prepareStatement(checkSql); ResultSet rs = ps.executeQuery()) {
                rs.next();
                int cnt = rs.getInt(1);
                if (cnt == 0) {
                    System.out.println("Column password_hash not found — adding column...");
                    try (Statement st = c.createStatement()) {
                        st.executeUpdate("ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) NULL");
                        System.out.println("Added column password_hash");
                    }
                } else {
                    System.out.println("Column password_hash already exists");
                }
            }

            // 2) optional: set password_hash = NULL for all users
            if (nullify) {
                try (Statement st = c.createStatement()) {
                    int updated = st.executeUpdate("UPDATE users SET password_hash = NULL");
                    System.out.println("Set password_hash = NULL for " + updated + " users");
                }
            }

            // 3) optional: create admin1/admin2/admin3 if missing
            if (createAdmins) {
                String[] admins = new String[] {"admin1", "admin2", "admin3"};
                for (int i = 0; i < admins.length; i++) {
                    String name = admins[i];
                    int branch = i + 1;
                    try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM users WHERE name = ? AND branch_id = ?")) {
                        ps.setString(1, name);
                        ps.setInt(2, branch);
                        try (ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            if (rs.getInt(1) == 0) {
                                try (PreparedStatement ins = c.prepareStatement("INSERT INTO users (name, role, email, phone, branch_id) VALUES (?, 'admin', ?, NULL, ?)")) {
                                    ins.setString(1, name);
                                    ins.setString(2, name + "@example.com");
                                    ins.setInt(3, branch);
                                    ins.executeUpdate();
                                    System.out.println("Inserted " + name + " into branch " + branch);
                                }
                            } else {
                                System.out.println(name + " already exists in branch " + branch);
                            }
                        }
                    }
                }
            }

            // 4) optional: create llegadas table if requested
            if (createLlegadas) {
                System.out.println("Ensuring table 'llegadas' exists...");
                String createLlegadasSql = "CREATE TABLE IF NOT EXISTS llegadas ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "product_id INT NOT NULL,"
                        + "quantity DOUBLE NOT NULL,"
                        + "recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + "branch_id INT NOT NULL)";
                try (Statement st = c.createStatement()) {
                    st.executeUpdate(createLlegadasSql);
                    System.out.println("Table 'llegadas' ensured (created if missing).");
                }
            }

            System.out.println("DB migration helper completed successfully.");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
