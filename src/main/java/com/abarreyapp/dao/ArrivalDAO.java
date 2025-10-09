package com.abarreyapp.dao;

import com.abarreyapp.db.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArrivalDAO {
    public List<String[]> findAll() throws SQLException {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT l.id, p.name AS product, l.quantity, l.recorded_at FROM llegadas l JOIN products p ON l.product_id = p.id WHERE l.branch_id = ? ORDER BY l.recorded_at DESC";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("product"), rs.getString("quantity"), rs.getString("recorded_at")});
                }
            }
        }
        return out;
    }

    public List<String[]> findByDateRange(Date start, Date end) throws SQLException {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT l.id, p.name AS product, l.quantity, l.recorded_at FROM llegadas l JOIN products p ON l.product_id = p.id WHERE l.branch_id = ? AND l.recorded_at BETWEEN ? AND ? ORDER BY l.recorded_at DESC";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            ps.setTimestamp(2, new Timestamp(start.getTime()));
            ps.setTimestamp(3, new Timestamp(end.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("product"), rs.getString("quantity"), rs.getString("recorded_at")});
                }
            }
        }
        return out;
    }

    public int insert(int productId, double quantity) throws SQLException {
        String sql = "INSERT INTO llegadas (product_id, quantity, branch_id) VALUES (?, ?, ?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, productId);
            ps.setDouble(2, quantity);
            ps.setInt(3, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
}
