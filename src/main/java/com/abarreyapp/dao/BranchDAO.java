package com.abarreyapp.dao;

import com.abarreyapp.db.DB;
import com.abarreyapp.model.Branch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {
    public List<Branch> findAll() throws SQLException {
        List<Branch> out = new ArrayList<>();
        String sql = "SELECT id, name FROM branches ORDER BY id";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Branch(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return out;
    }
}
