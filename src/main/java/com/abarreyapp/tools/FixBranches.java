package com.abarreyapp.tools;

import com.abarreyapp.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class FixBranches {
    public static void main(String[] args) {
        System.out.println("Fixing branch names...");
        try (Connection c = DB.getConnection()) {
            // Update branches by id to human-friendly names
            try (PreparedStatement ps = c.prepareStatement("UPDATE branches SET name = ? WHERE id = ?")) {
                ps.setString(1, "Sucursal Central"); ps.setInt(2, 1); ps.executeUpdate();
                ps.setString(1, "Sucursal Norte"); ps.setInt(2, 2); ps.executeUpdate();
                ps.setString(1, "Sucursal Sur"); ps.setInt(2, 3); ps.executeUpdate();
            }

            System.out.println("Updated branch names. Current branches:");
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT id, name FROM branches ORDER BY id")) {
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + "\t" + rs.getString("name"));
                }
            }

            System.out.println("Done.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
