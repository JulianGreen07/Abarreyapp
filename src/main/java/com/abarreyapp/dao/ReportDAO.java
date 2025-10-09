package com.abarreyapp.dao;

import com.abarreyapp.db.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    public List<String[]> totalMermaByProduct(Date start, Date end) throws SQLException {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT p.name, SUM(m.weight) AS total_weight FROM mermas m JOIN products p ON m.product_id = p.id WHERE m.branch_id = ? AND m.recorded_at BETWEEN ? AND ? GROUP BY p.name ORDER BY total_weight DESC";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, com.abarreyapp.db.BranchContext.getCurrentBranchId());
            ps.setTimestamp(2, new Timestamp(start.getTime()));
            ps.setTimestamp(3, new Timestamp(end.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{rs.getString(1), String.valueOf(rs.getDouble(2))});
                }
            }
        }
        return out;
    }

    // convenience overload accepting java.util.Date
    public List<String[]> totalMermaByProduct(java.util.Date start, java.util.Date end) throws SQLException {
        return totalMermaByProduct(new Date(start.getTime()), new Date(end.getTime()));
    }
}
