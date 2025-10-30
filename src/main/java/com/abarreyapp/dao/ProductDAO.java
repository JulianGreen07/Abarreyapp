package com.abarreyapp.dao;

import com.abarreyapp.db.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<String[]> findAll() throws SQLException {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT id, name, category, stock FROM products WHERE branch_id = ? ORDER BY id";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("name"), rs.getString("category"), rs.getString("stock")});
                }
            }
        }
        return out;
    }

    public int insert(String name, String category, String stock) throws SQLException {
        String sql = "INSERT INTO products (name, category, stock, branch_id) VALUES (?, ?, ?, ?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, stock);
            ps.setInt(4, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            int rows = ps.executeUpdate();
            logOp("INSERT name=" + name + " rows=" + rows);
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            // Fallback: try to find inserted id by name (last inserted in this branch)
            try (Connection c2 = DB.getConnection(); PreparedStatement ps2 = c2.prepareStatement("SELECT id FROM products WHERE name = ? AND branch_id = ? ORDER BY id DESC LIMIT 1")) {
                ps2.setString(1, name);
                ps2.setInt(2, com.abarreyapp.db.BranchContext.getCurrentBranchId());
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            } catch (SQLException ignore) {}
        }
        return -1;
    }

    private static void logOp(String s) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileOutputStream("app_db_ops.log", true))) {
            pw.println(java.time.LocalDateTime.now() + " - " + s);
        } catch (Exception ignored) {}
    }

    public Integer findByName(String name) throws SQLException {
        String sql = "SELECT id FROM products WHERE name = ? AND branch_id = ? LIMIT 1";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return null;
    }

    public boolean update(int id, String name, String category, String stock) throws SQLException {
        String sql = "UPDATE products SET name=?, category=?, stock=? WHERE id=? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, stock);
            ps.setInt(4, id);
            ps.setInt(5, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            int rows = ps.executeUpdate();
            logOp("UPDATE id=" + id + " rows=" + rows + " name=" + name);
            return rows > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            int rows = ps.executeUpdate();
            logOp("DELETE id=" + id + " rows=" + rows);
            return rows > 0;
        }
    }
}
