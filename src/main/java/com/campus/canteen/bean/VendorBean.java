package com.campus.canteen.bean;

import com.campus.canteen.dao.MenuItemDAO;
import com.campus.canteen.dao.OrderDAO;
import com.campus.canteen.dao.VendorDAO;
import com.campus.canteen.model.MenuItem;
import com.campus.canteen.model.Order;
import com.campus.canteen.model.Vendor;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "vendorBean")
@ViewScoped
public class VendorBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{authBean}")
    private AuthBean authBean;

    private VendorDAO vendorDAO;
    private MenuItemDAO menuItemDAO;
    private OrderDAO orderDAO;

    private Vendor vendor;
    private List<MenuItem> menuList;
    private List<Order> orderList;
    private String orderFilter = "ALL";

    // For creating or editing menu item
    private MenuItem itemForm = new MenuItem();
    private boolean editMode = false;

    // Metrics
    private int placedCount;
    private int preparingCount;
    private int readyCount;
    private int collectedCount;
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @PostConstruct
    public void init() {
        vendorDAO = new VendorDAO();
        menuItemDAO = new MenuItemDAO();
        orderDAO = new OrderDAO();
        loadVendorData();
    }

    public void loadVendorData() {
        if (authBean != null && authBean.getCurrentUser() != null && authBean.isVendor()) {
            this.vendor = vendorDAO.findByUserId(authBean.getCurrentUser().getId());
            if (this.vendor != null) {
                refreshMenu();
                refreshOrders();
                loadMetrics();
            }
        }
    }

    public void refreshMenu() {
        if (vendor != null) {
            menuList = menuItemDAO.findByVendorId(vendor.getId());
        }
    }

    public void refreshOrders() {
        if (vendor != null) {
            List<Order> all = orderDAO.getOrdersByVendor(vendor.getId());
            if ("ALL".equalsIgnoreCase(orderFilter)) {
                this.orderList = all;
            } else {
                this.orderList = new ArrayList<>();
                for (Order o : all) {
                    if (orderFilter.equalsIgnoreCase(o.getStatus())) {
                        this.orderList.add(o);
                    }
                }
            }
        }
    }

    public void loadMetrics() {
        if (vendor != null) {
            int vId = vendor.getId();
            placedCount = orderDAO.countOrdersByStatus(vId, "PLACED");
            preparingCount = orderDAO.countOrdersByStatus(vId, "PREPARING");
            readyCount = orderDAO.countOrdersByStatus(vId, "READY");
            collectedCount = orderDAO.countOrdersByStatus(vId, "COLLECTED");
            totalRevenue = orderDAO.getVendorSalesRevenue(vId);
        }
    }

    public void filterOrders(String status) {
        this.orderFilter = status;
        refreshOrders();
    }

    // Order status progression
    public void advanceOrderStatus(Order order, String newStatus) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (order != null) {
            boolean ok = orderDAO.updateStatus(order.getId(), newStatus);
            if (ok) {
                order.setStatus(newStatus);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Updated", "Order " + order.getOrderNumber() + " updated to " + newStatus));
                refreshOrders();
                loadMetrics();
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update order status."));
            }
        }
    }

    // Menu actions
    public void prepareNewItem() {
        this.itemForm = new MenuItem();
        if (this.vendor != null) {
            this.itemForm.setVendorId(this.vendor.getId());
        }
        this.itemForm.setCategory("Snacks");
        this.editMode = false;
    }

    public void prepareEditItem(MenuItem item) {
        this.itemForm = new MenuItem();
        this.itemForm.setId(item.getId());
        this.itemForm.setVendorId(item.getVendorId());
        this.itemForm.setItemName(item.getItemName());
        this.itemForm.setCategory(item.getCategory());
        this.itemForm.setDescription(item.getDescription());
        this.itemForm.setPrice(item.getPrice());
        this.itemForm.setAvailable(item.isAvailable());
        this.itemForm.setPreparationTimeMins(item.getPreparationTimeMins());
        this.editMode = true;
    }

    public void saveMenuItem() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (itemForm.getItemName() == null || itemForm.getItemName().trim().isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Item name is required."));
            return;
        }
        if (itemForm.getPrice() == null || itemForm.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Valid price is required."));
            return;
        }

        if (editMode) {
            boolean success = menuItemDAO.update(itemForm);
            if (success) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Saved", "Item updated successfully!"));
                refreshMenu();
                prepareNewItem();
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update item."));
            }
        } else {
            itemForm.setVendorId(vendor.getId());
            boolean success = menuItemDAO.insert(itemForm);
            if (success) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Added", "New item added to menu!"));
                refreshMenu();
                prepareNewItem();
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add item."));
            }
        }
    }

    public void toggleAvailability(MenuItem item) {
        if (item != null) {
            boolean nextState = !item.isAvailable();
            boolean ok = menuItemDAO.toggleAvailability(item.getId(), nextState);
            if (ok) {
                item.setAvailable(nextState);
            }
        }
    }

    public void deleteMenuItem(MenuItem item) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null && vendor != null) {
            boolean ok = menuItemDAO.delete(item.getId(), vendor.getId());
            if (ok) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted", "Menu item removed."));
                refreshMenu();
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cannot delete item with active orders."));
            }
        }
    }

    // Getters and Setters
    public AuthBean getAuthBean() { return authBean; }
    public void setAuthBean(AuthBean authBean) { this.authBean = authBean; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }

    public List<MenuItem> getMenuList() { return menuList; }
    public void setMenuList(List<MenuItem> menuList) { this.menuList = menuList; }

    public List<Order> getOrderList() { return orderList; }
    public void setOrderList(List<Order> orderList) { this.orderList = orderList; }

    public String getOrderFilter() { return orderFilter; }
    public void setOrderFilter(String orderFilter) { this.orderFilter = orderFilter; }

    public MenuItem getItemForm() { return itemForm; }
    public void setItemForm(MenuItem itemForm) { this.itemForm = itemForm; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }

    public int getPlacedCount() { return placedCount; }
    public int getPreparingCount() { return preparingCount; }
    public int getReadyCount() { return readyCount; }
    public int getCollectedCount() { return collectedCount; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}
