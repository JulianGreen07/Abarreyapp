package com.abarreyapp.dao;

import com.abarreyapp.db.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MermaDAO {
    public List<String[]> findAll() throws SQLException {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT m.id, p.name AS product, m.weight, m.recorded_at FROM mermas m JOIN products p ON m.product_id = p.id WHERE m.branch_id = ? ORDER BY m.recorded_at DESC";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("product"), rs.getString("weight"), rs.getString("recorded_at")});
            }
            }
        }
        return out;
    }

    public List<String[]> findByDateRange(java.util.Date start, java.util.Date end) throws SQLException {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT m.id, p.name AS product, m.weight, m.recorded_at FROM mermas m JOIN products p ON m.product_id = p.id WHERE m.branch_id = ? AND m.recorded_at BETWEEN ? AND ? ORDER BY m.recorded_at DESC";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            ps.setTimestamp(2, new Timestamp(start.getTime()));
            ps.setTimestamp(3, new Timestamp(end.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("product"), rs.getString("weight"), rs.getString("recorded_at")});
                }
            }
        }
        return out;
    }

    public int insert(int productId, double weight) throws SQLException {
        String sql = "INSERT INTO mermas (product_id, weight, branch_id) VALUES (?, ?, ?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, productId);
            ps.setDouble(2, weight);
            ps.setInt(3, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mermas WHERE id = ? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(int id, double weight, String recordedAt) throws SQLException {
        String sql = "UPDATE mermas SET weight = ?, recorded_at = ? WHERE id = ? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, weight);
            ps.setString(2, recordedAt);
            ps.setInt(3, id);
            ps.setInt(4, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            return ps.executeUpdate() > 0;
        }
    }
}
