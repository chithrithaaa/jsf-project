package com.campus.canteen.dao;

import com.campus.canteen.model.Vendor;
import com.campus.canteen.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendorDAO {

    public Vendor findById(int id) {
        String sql = "SELECT v.id, v.user_id, v.stall_name, v.counter_number, v.cuisine_type, v.description, v.is_active, v.created_at, " +
                     "u.full_name as owner_name, u.email as owner_email, u.phone as owner_phone " +
                     "FROM vendors v JOIN users u ON v.user_id = u.id WHERE v.id = ?";
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

    public Vendor findByUserId(int userId) {
        String sql = "SELECT v.id, v.user_id, v.stall_name, v.counter_number, v.cuisine_type, v.description, v.is_active, v.created_at, " +
                     "u.full_name as owner_name, u.email as owner_email, u.phone as owner_phone " +
                     "FROM vendors v JOIN users u ON v.user_id = u.id WHERE v.user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
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

    public List<Vendor> findAllActiveVendors() {
        List<Vendor> list = new ArrayList<>();
        String sql = "SELECT v.id, v.user_id, v.stall_name, v.counter_number, v.cuisine_type, v.description, v.is_active, v.created_at, " +
                     "u.full_name as owner_name, u.email as owner_email, u.phone as owner_phone " +
                     "FROM vendors v JOIN users u ON v.user_id = u.id WHERE v.is_active = TRUE ORDER BY v.stall_name ASC";
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

    public List<Vendor> findAllVendors() {
        List<Vendor> list = new ArrayList<>();
        String sql = "SELECT v.id, v.user_id, v.stall_name, v.counter_number, v.cuisine_type, v.description, v.is_active, v.created_at, " +
                     "u.full_name as owner_name, u.email as owner_email, u.phone as owner_phone " +
                     "FROM vendors v JOIN users u ON v.user_id = u.id ORDER BY v.id ASC";
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

    public boolean createVendor(Vendor v) {
        String sql = "INSERT INTO vendors (user_id, stall_name, counter_number, cuisine_type, description, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getUserId());
            ps.setString(2, v.getStallName());
            ps.setString(3, v.getCounterNumber());
            ps.setString(4, v.getCuisineType());
            ps.setString(5, v.getDescription());
            ps.setBoolean(6, v.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        v.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateVendor(Vendor v) {
        String sql = "UPDATE vendors SET stall_name = ?, counter_number = ?, cuisine_type = ?, description = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getStallName());
            ps.setString(2, v.getCounterNumber());
            ps.setString(3, v.getCuisineType());
            ps.setString(4, v.getDescription());
            ps.setBoolean(5, v.isActive());
            ps.setInt(6, v.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countVendors() {
        String sql = "SELECT COUNT(*) FROM vendors";
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

    private Vendor mapRow(ResultSet rs) throws SQLException {
        Vendor v = new Vendor();
        v.setId(rs.getInt("id"));
        v.setUserId(rs.getInt("user_id"));
        v.setStallName(rs.getString("stall_name"));
        v.setCounterNumber(rs.getString("counter_number"));
        v.setCuisineType(rs.getString("cuisine_type"));
        v.setDescription(rs.getString("description"));
        v.setActive(rs.getBoolean("is_active"));
        v.setCreatedAt(rs.getTimestamp("created_at"));
        v.setOwnerName(rs.getString("owner_name"));
        v.setOwnerEmail(rs.getString("owner_email"));
        v.setOwnerPhone(rs.getString("owner_phone"));
        return v;
    }
}
