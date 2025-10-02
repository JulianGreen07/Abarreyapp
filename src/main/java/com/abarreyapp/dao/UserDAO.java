package com.abarreyapp.dao;

import com.abarreyapp.db.DB;
import com.abarreyapp.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name, role, email, phone FROM users WHERE branch_id = ? ORDER BY id";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("role"), rs.getString("email"), rs.getString("phone")));
                }
            }
        }
        return list;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT id, name, role, email, phone FROM users WHERE id = ? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new User(rs.getInt("id"), rs.getString("name"), rs.getString("role"), rs.getString("email"), rs.getString("phone"));
            }
        }
        return null;
    }

    public User findByName(String name) throws SQLException {
        String sql = "SELECT id, name, role, email, phone, password_hash FROM users WHERE name = ? AND branch_id = ? LIMIT 1";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getInt("id"), rs.getString("name"), rs.getString("role"), rs.getString("email"), rs.getString("phone"));
                    try { u.setPasswordHash(rs.getString("password_hash")); } catch (Exception ex) {}
                    return u;
                }
            }
        }
        return null;
    }

    public boolean updatePasswordHash(int userId, String hash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setInt(2, userId);
            ps.setInt(3, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            return ps.executeUpdate() > 0;
        }
    }

    public int insert(User u) throws SQLException {
        String sql = "INSERT INTO users (name, role, email, phone, branch_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getRole());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPhone());
            ps.setInt(5, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean update(User u) throws SQLException {
        String sql = "UPDATE users SET name=?, role=?, email=?, phone=? WHERE id=? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getRole());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPhone());
            ps.setInt(5, u.getId());
            ps.setInt(6, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            return ps.executeUpdate() > 0;
        }
    }
}
