package com.abarreyapp.tools;

import com.abarreyapp.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbListUsers {
    public static void main(String[] args) {
        System.out.println("Listing users (id, name, password_hash, branch_id):");
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id, name, password_hash, branch_id FROM users ORDER BY branch_id, id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String ph = rs.getString("password_hash");
                    int bid = rs.getInt("branch_id");
                    System.out.println(id + "\t" + name + "\t" + (ph==null?"NULL":(ph.length()>8?ph.substring(0,8)+"...":ph)) + "\tbranch=" + bid);
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing users: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
