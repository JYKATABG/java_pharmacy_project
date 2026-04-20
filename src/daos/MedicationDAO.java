package daos;

import models.Medication;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseManager;

public class MedicationDAO {

    public List<Medication> getAll() {
        List<Medication> list = new ArrayList<>();
        String sql = """
                SELECT m.*, c.category_name
                FROM medications m
                JOIN categories c ON m.category_id = c.id
                ORDER BY m.id DESC
                """;
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Medication> searchByName(String keyword) {
        List<Medication> list = new ArrayList<>();
        String sql = """
                SELECT m.*, c.category_name
                FROM medications m
                JOIN categories c ON m.category_id = c.id
                WHERE LOWER(m.name) LIKE LOWER(?)
                """;
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insert(Medication m) {
        String sql = "INSERT INTO medications (name, manufacturer, price, stock, requires_prescription, category_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getManufacturer());
            ps.setDouble(3, m.getPrice());
            ps.setInt(4, m.getStock());
            ps.setBoolean(5, m.isRequiresPrescription());
            ps.setInt(6, m.getCategoryId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Medication m) {
        String sql = "UPDATE medications SET name=?, manufacturer=?, price=?, stock=?, requires_prescription=?, category_id=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getManufacturer());
            ps.setDouble(3, m.getPrice());
            ps.setInt(4, m.getStock());
            ps.setBoolean(5, m.isRequiresPrescription());
            ps.setInt(6, m.getCategoryId());
            ps.setInt(7, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM medications WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private models.Medication mapRow(ResultSet rs) throws SQLException {
        models.Medication m = new Medication();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("name"));
        m.setManufacturer(rs.getString("manufacturer"));
        m.setPrice(rs.getDouble("price"));
        m.setStock(rs.getInt("stock"));
        m.setRequiresPrescription(rs.getBoolean("requires_prescription"));
        m.setCategoryId(rs.getInt("category_id"));
        m.setCategoryName(rs.getString("category_name"));
        return m;
    }
}