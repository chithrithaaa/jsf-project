package com.campus.canteen.dao;

import com.campus.canteen.model.Order;
import com.campus.canteen.model.OrderItem;
import com.campus.canteen.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO {

    public String generateOrderNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        int randomDigits = (int) (Math.random() * 900) + 100;
        return "ORD-" + sdf.format(new Date()) + "-" + randomDigits;
    }

    public boolean placeOrder(Order order) {
        String insertOrderSql = "INSERT INTO orders (order_number, student_id, vendor_id, total_amount, status, notes) VALUES (?, ?, ?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, menu_item_id, item_name, unit_price, quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // start transaction

            try (PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, order.getOrderNumber());
                psOrder.setInt(2, order.getStudentId());
                psOrder.setInt(3, order.getVendorId());
                psOrder.setBigDecimal(4, order.getTotalAmount());
                psOrder.setString(5, "PLACED");
                psOrder.setString(6, order.getNotes());

                int affected = psOrder.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                int orderId = 0;
                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                        order.setId(orderId);
                    }
                }

                if (orderId == 0) {
                    conn.rollback();
                    return false;
                }

                try (PreparedStatement psItem = conn.prepareStatement(insertItemSql)) {
                    for (OrderItem item : order.getItems()) {
                        psItem.setInt(1, orderId);
                        psItem.setInt(2, item.getMenuItemId());
                        psItem.setString(3, item.getItemName());
                        psItem.setBigDecimal(4, item.getUnitPrice());
                        psItem.setInt(5, item.getQuantity());
                        psItem.setBigDecimal(6, item.getSubtotal());
                        psItem.addBatch();
                    }
                    psItem.executeBatch();
                }

                conn.commit(); // commit transaction
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeQuietly(conn);
        }
        return false;
    }

    public List<Order> getOrdersByStudent(int studentId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.id, o.order_number, o.student_id, o.vendor_id, o.total_amount, o.status, " +
                     "o.order_time, o.pickup_time, o.notes, v.stall_name, v.counter_number, u.full_name as student_name " +
                     "FROM orders o " +
                     "JOIN vendors v ON o.vendor_id = v.id " +
                     "JOIN users u ON o.student_id = u.id " +
                     "WHERE o.student_id = ? ORDER BY o.id DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(getOrderItems(conn, order.getId()));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> getOrdersByVendor(int vendorId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.id, o.order_number, o.student_id, o.vendor_id, o.total_amount, o.status, " +
                     "o.order_time, o.pickup_time, o.notes, v.stall_name, v.counter_number, u.full_name as student_name " +
                     "FROM orders o " +
                     "JOIN vendors v ON o.vendor_id = v.id " +
                     "JOIN users u ON o.student_id = u.id " +
                     "WHERE o.vendor_id = ? ORDER BY o.id DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(getOrderItems(conn, order.getId()));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.id, o.order_number, o.student_id, o.vendor_id, o.total_amount, o.status, " +
                     "o.order_time, o.pickup_time, o.notes, v.stall_name, v.counter_number, u.full_name as student_name " +
                     "FROM orders o " +
                     "JOIN vendors v ON o.vendor_id = v.id " +
                     "JOIN users u ON o.student_id = u.id " +
                     "ORDER BY o.id DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = mapOrderRow(rs);
                order.setItems(getOrderItems(conn, order.getId()));
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT o.id, o.order_number, o.student_id, o.vendor_id, o.total_amount, o.status, " +
                     "o.order_time, o.pickup_time, o.notes, v.stall_name, v.counter_number, u.full_name as student_name " +
                     "FROM orders o " +
                     "JOIN vendors v ON o.vendor_id = v.id " +
                     "JOIN users u ON o.student_id = u.id " +
                     "WHERE o.id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(getOrderItems(conn, order.getId()));
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ?, pickup_time = CASE WHEN ? = 'COLLECTED' THEN NOW() ELSE pickup_time END WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cancelOrder(int orderId, int studentId) {
        String sql = "UPDATE orders SET status = 'CANCELLED' WHERE id = ? AND student_id = ? AND status = 'PLACED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public BigDecimal getTotalSalesRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'COLLECTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getVendorSalesRevenue(int vendorId) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE vendor_id = ? AND status = 'COLLECTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public int countOrdersByStatus(Integer vendorId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM orders WHERE status = ?");
        if (vendorId != null) {
            sql.append(" AND vendor_id = ?");
        }
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, status);
            if (vendorId != null) {
                ps.setInt(2, vendorId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private List<OrderItem> getOrderItems(Connection conn, int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT id, order_id, menu_item_id, item_name, unit_price, quantity, subtotal FROM order_items WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setMenuItemId(rs.getInt("menu_item_id"));
                    item.setItemName(rs.getString("item_name"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setSubtotal(rs.getBigDecimal("subtotal"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setOrderNumber(rs.getString("order_number"));
        o.setStudentId(rs.getInt("student_id"));
        o.setStudentName(rs.getString("student_name"));
        o.setVendorId(rs.getInt("vendor_id"));
        o.setVendorName(rs.getString("stall_name"));
        o.setCounterNumber(rs.getString("counter_number"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setOrderTime(rs.getTimestamp("order_time"));
        o.setPickupTime(rs.getTimestamp("pickup_time"));
        o.setNotes(rs.getString("notes"));
        return o;
    }
}
