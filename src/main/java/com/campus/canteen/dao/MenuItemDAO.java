package com.campus.canteen.dao;

import com.campus.canteen.model.MenuItem;
import com.campus.canteen.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {

    public MenuItem findById(int id) {
        String sql = "SELECT m.id, m.vendor_id, m.item_name, m.category, m.description, m.price, m.is_available, " +
                     "m.preparation_time_mins, m.created_at, v.stall_name, v.counter_number " +
                     "FROM menu_items m JOIN vendors v ON m.vendor_id = v.id WHERE m.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MenuItem> findAllAvailable() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT m.id, m.vendor_id, m.item_name, m.category, m.description, m.price, m.is_available, " +
                     "m.preparation_time_mins, m.created_at, v.stall_name, v.counter_number " +
                     "FROM menu_items m JOIN vendors v ON m.vendor_id = v.id " +
                     "WHERE m.is_available = TRUE AND v.is_active = TRUE ORDER BY m.category, m.item_name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MenuItem> findByVendorId(int vendorId) {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT m.id, m.vendor_id, m.item_name, m.category, m.description, m.price, m.is_available, " +
                     "m.preparation_time_mins, m.created_at, v.stall_name, v.counter_number " +
                     "FROM menu_items m JOIN vendors v ON m.vendor_id = v.id WHERE m.vendor_id = ? ORDER BY m.id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MenuItem> filter(Integer vendorId, String category, String keyword) {
        List<MenuItem> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT m.id, m.vendor_id, m.item_name, m.category, m.description, m.price, m.is_available, " +
            "m.preparation_time_mins, m.created_at, v.stall_name, v.counter_number " +
            "FROM menu_items m JOIN vendors v ON m.vendor_id = v.id WHERE m.is_available = TRUE AND v.is_active = TRUE "
        );

        List<Object> params = new ArrayList<>();
        if (vendorId != null && vendorId > 0) {
            sql.append(" AND m.vendor_id = ?");
            params.add(vendorId);
        }
        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            sql.append(" AND m.category = ?");
            params.add(category);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(m.item_name) LIKE ? OR LOWER(m.description) LIKE ?)");
            String pattern = "%" + keyword.toLowerCase().trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        sql.append(" ORDER BY m.item_name ASC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(MenuItem item) {
        String sql = "INSERT INTO menu_items (vendor_id, item_name, category, description, price, is_available, preparation_time_mins) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getVendorId());
            ps.setString(2, item.getItemName());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getDescription());
            ps.setBigDecimal(5, item.getPrice());
            ps.setBoolean(6, item.isAvailable());
            ps.setInt(7, item.getPreparationTimeMins());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        item.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(MenuItem item) {
        String sql = "UPDATE menu_items SET item_name = ?, category = ?, description = ?, price = ?, is_available = ?, preparation_time_mins = ? WHERE id = ? AND vendor_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getItemName());
            ps.setString(2, item.getCategory());
            ps.setString(3, item.getDescription());
            ps.setBigDecimal(4, item.getPrice());
            ps.setBoolean(5, item.isAvailable());
            ps.setInt(6, item.getPreparationTimeMins());
            ps.setInt(7, item.getId());
            ps.setInt(8, item.getVendorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id, int vendorId) {
        String sql = "DELETE FROM menu_items WHERE id = ? AND vendor_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, vendorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleAvailability(int id, boolean isAvailable) {
        String sql = "UPDATE menu_items SET is_available = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isAvailable);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countTotalItems() {
        String sql = "SELECT COUNT(*) FROM menu_items";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private MenuItem mapRow(ResultSet rs) throws SQLException {
        MenuItem m = new MenuItem();
        m.setId(rs.getInt("id"));
        m.setVendorId(rs.getInt("vendor_id"));
        m.setItemName(rs.getString("item_name"));
        m.setCategory(rs.getString("category"));
        m.setDescription(rs.getString("description"));
        m.setPrice(rs.getBigDecimal("price"));
        m.setAvailable(rs.getBoolean("is_available"));
        m.setPreparationTimeMins(rs.getInt("preparation_time_mins"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        m.setVendorName(rs.getString("stall_name"));
        m.setStallCounter(rs.getString("counter_number"));
        return m;
    }
}
