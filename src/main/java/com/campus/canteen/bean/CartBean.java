package com.campus.canteen.bean;

import com.campus.canteen.dao.OrderDAO;
import com.campus.canteen.model.CartItem;
import com.campus.canteen.model.MenuItem;
import com.campus.canteen.model.Order;
import com.campus.canteen.model.OrderItem;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "cartBean")
@SessionScoped
public class CartBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<CartItem> items;
    private OrderDAO orderDAO;
    private String orderNotes;
    private List<Order> lastPlacedOrders;

    @PostConstruct
    public void init() {
        items = new ArrayList<>();
        orderDAO = new OrderDAO();
        lastPlacedOrders = new ArrayList<>();
    }

    public void addToCart(MenuItem item) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item == null || !item.isAvailable()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Unavailable", "This item is currently sold out."));
            return;
        }

        // Check if item already exists in cart
        for (CartItem ci : items) {
            if (ci.getMenuItem().getId() == item.getId()) {
                ci.incrementQuantity();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Updated", "Increased quantity for " + item.getItemName()));
                return;
            }
        }

        // Add new item
        items.add(new CartItem(item, 1));
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Added", item.getItemName() + " added to your tray!"));
    }

    public void increment(CartItem item) {
        if (item != null) {
            item.incrementQuantity();
        }
    }

    public void decrement(CartItem item) {
        if (item != null) {
            if (item.getQuantity() > 1) {
                item.decrementQuantity();
            } else {
                items.remove(item);
            }
        }
    }

    public void removeItem(CartItem item) {
        if (item != null) {
            items.remove(item);
        }
    }

    public void clearCart() {
        items.clear();
        orderNotes = "";
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : items) {
            total = total.add(ci.getSubtotal());
        }
        return total;
    }

    public int getTotalItemCount() {
        int count = 0;
        for (CartItem ci : items) {
            count += ci.getQuantity();
        }
        return count;
    }

    public String checkout(AuthBean authBean) {
        FacesContext context = FacesContext.getCurrentInstance();

        if (authBean == null || !authBean.isLoggedIn()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Required", "Please log in as a student to place an order."));
            return "/login.xhtml?faces-redirect=true";
        }

        if (items.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Cart Empty", "Your cart is empty. Add items from the menu first."));
            return null;
        }

        // Group cart items by vendorId so each vendor gets a separate order ticket
        Map<Integer, List<CartItem>> vendorGroups = new HashMap<>();
        for (CartItem ci : items) {
            int vId = ci.getMenuItem().getVendorId();
            vendorGroups.computeIfAbsent(vId, k -> new ArrayList<>()).add(ci);
        }

        lastPlacedOrders.clear();
        boolean allSuccess = true;

        for (Map.Entry<Integer, List<CartItem>> entry : vendorGroups.entrySet()) {
            int vId = entry.getKey();
            List<CartItem> vItems = entry.getValue();

            BigDecimal stallTotal = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem ci : vItems) {
                stallTotal = stallTotal.add(ci.getSubtotal());
                OrderItem oi = new OrderItem();
                oi.setMenuItemId(ci.getMenuItem().getId());
                oi.setItemName(ci.getMenuItem().getItemName());
                oi.setUnitPrice(ci.getMenuItem().getPrice());
                oi.setQuantity(ci.getQuantity());
                oi.setSubtotal(ci.getSubtotal());
                orderItems.add(oi);
            }

            Order order = new Order();
            order.setOrderNumber(orderDAO.generateOrderNumber());
            order.setStudentId(authBean.getCurrentUser().getId());
            order.setVendorId(vId);
            order.setTotalAmount(stallTotal);
            order.setStatus("PLACED");
            order.setNotes(orderNotes);
            order.setItems(orderItems);

            boolean placed = orderDAO.placeOrder(order);
            if (placed) {
                // Fetch complete details with vendor stall name
                Order fetched = orderDAO.getOrderById(order.getId());
                lastPlacedOrders.add(fetched != null ? fetched : order);
            } else {
                allSuccess = false;
            }
        }

        if (allSuccess) {
            clearCart();
            return "/order-confirmation.xhtml?faces-redirect=true";
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "There was an issue creating one or more orders. Please try again."));
            return null;
        }
    }

    // Getters and Setters
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }

    public List<Order> getLastPlacedOrders() { return lastPlacedOrders; }
    public void setLastPlacedOrders(List<Order> lastPlacedOrders) { this.lastPlacedOrders = lastPlacedOrders; }
}
