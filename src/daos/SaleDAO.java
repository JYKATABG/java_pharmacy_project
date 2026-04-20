package daos;

import db.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Sale;

public class SaleDAO {

    public List<Sale> getAll() {
        List<Sale> list = new ArrayList<>();
        String sql = """
                SELECT s.*,
                       c.first_name || ' ' || c.last_name AS client_name,
                       m.name AS medication_name
                FROM sales s
                JOIN clients c ON s.client_id = c.id
                JOIN medications m ON s.medication_id = m.id
                ORDER BY s.id DESC
                """;
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Sale> searchByClientName(String keyword) {
        List<Sale> list = new ArrayList<>();
        String sql = """
                SELECT s.*,
                       c.first_name || ' ' || c.last_name AS client_name,
                       m.name AS medication_name
                FROM sales s
                JOIN clients c ON s.client_id = c.id
                JOIN medications m ON s.medication_id = m.id
                WHERE LOWER(c.last_name) LIKE LOWER(?)
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void insert(Sale s) {
        String sql = "INSERT INTO sales (client_id, medication_id, sale_date, quantity, total_price, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getClientId());
            ps.setInt(2, s.getMedicationId());
            ps.setDate(3, Date.valueOf(s.getSaleDate()));
            ps.setInt(4, s.getQuantity());
            ps.setDouble(5, s.getTotalPrice());
            ps.setString(6, s.getPaymentMethod());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(Sale s) {
        String sql = "UPDATE sales SET client_id=?, medication_id=?, sale_date=?, quantity=?, total_price=?, payment_method=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getClientId());
            ps.setInt(2, s.getMedicationId());
            ps.setDate(3, Date.valueOf(s.getSaleDate()));
            ps.setInt(4, s.getQuantity());
            ps.setDouble(5, s.getTotalPrice());
            ps.setString(6, s.getPaymentMethod());
            ps.setInt(7, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM sales WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Sale mapRow(ResultSet rs) throws SQLException {
        Sale s = new Sale();
        s.setId(rs.getInt("id"));
        s.setClientId(rs.getInt("client_id"));
        s.setMedicationId(rs.getInt("medication_id"));
        s.setSaleDate(rs.getDate("sale_date").toLocalDate());
        s.setQuantity(rs.getInt("quantity"));
        s.setTotalPrice(rs.getDouble("total_price"));
        s.setPaymentMethod(rs.getString("payment_method"));
        return s;
    }
}
