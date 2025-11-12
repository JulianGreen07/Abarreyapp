package com.abarreyapp.dao;

import com.abarreyapp.db.DB;
import com.abarreyapp.db.BranchContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
// --- ¡NUEVAS IMPORTACIONES! ---
import java.util.ArrayList;
import java.util.List;
// --- FIN DE IMPORTACIONES ---

public class MermaDAO {

    /**
     * Devuelve todos los registros de merma de la sucursal actual.
     * (Este método ya lo tenías)
     */
    public List<Object[]> findAll() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        // Formateamos la fecha a yyyy-MM-dd para que coincida con el diálogo
        String sql = "SELECT id, product_name, quantity, DATE_FORMAT(waste_date, '%Y-%m-%d') as waste_date_str " +
                "FROM mermas " +
                "WHERE branch_id = ? " +
                "ORDER BY waste_date DESC, id DESC";

        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, BranchContext.getCurrentBranchId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getInt("id"),
                            rs.getString("product_name"),
                            rs.getString("quantity"),
                            rs.getString("waste_date_str") // Devolvemos la fecha como String
                    });
                }
            }
        }
        return list;
    }

    // --- (NUEVO MÉTODO AÑADIDO PARA REPORTES) ---
    /**
     * Busca registros de merma para el reporte, dentro de un rango de fechas.
     */
    public List<Object[]> findForReport(Date startDate, Date endDate) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        // Devuelve [Nombre, Cantidad, Fecha]
        String sql = "SELECT product_name, quantity, DATE_FORMAT(waste_date, '%Y-%m-%d') as date_str " +
                "FROM mermas " +
                "WHERE branch_id = ? AND waste_date BETWEEN ? AND ? " +
                "ORDER BY product_name, waste_date";

        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, BranchContext.getCurrentBranchId());
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("product_name"),
                            rs.getString("quantity"),
                            rs.getString("date_str")
                    });
                }
            }
        }
        return list;
    }
    // --- (FIN DEL NUEVO MÉTODO) ---


    /**
     * Inserta un nuevo registro de merma en la base de datos.
     * (Este método ya lo tenías)
     */
    public int insert(String productName, String quantity, Date wasteDate) throws SQLException {
        String sql = "INSERT INTO mermas (product_name, quantity, waste_date, branch_id) VALUES (?, ?, ?, ?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, productName);
            ps.setString(2, quantity);
            ps.setDate(3, wasteDate);
            ps.setInt(4, BranchContext.getCurrentBranchId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Actualiza un registro de merma existente.
     * (Este método ya lo tenías)
     */
    public boolean update(int id, String productName, String quantity, Date wasteDate) throws SQLException {
        String sql = "UPDATE mermas SET product_name = ?, quantity = ?, waste_date = ? WHERE id = ? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, productName);
            ps.setString(2, quantity);
            ps.setDate(3, wasteDate);
            ps.setInt(4, id);
            ps.setInt(5, BranchContext.getCurrentBranchId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un registro de merma de la base de datos.
     * (Este método ya lo tenías)
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mermas WHERE id = ? AND branch_id = ?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, BranchContext.getCurrentBranchId());

            return ps.executeUpdate() > 0;
        }
    }
}