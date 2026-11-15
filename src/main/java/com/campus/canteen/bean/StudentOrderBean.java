package com.campus.canteen.bean;

import com.campus.canteen.dao.OrderDAO;
import com.campus.canteen.model.Order;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "studentOrderBean")
@ViewScoped
public class StudentOrderBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{authBean}")
    private AuthBean authBean;

    private OrderDAO orderDAO;
    private List<Order> studentOrders;
    private Order selectedOrder;

    @PostConstruct
    public void init() {
        orderDAO = new OrderDAO();
        refreshOrders();
    }

    public void refreshOrders() {
        if (authBean != null && authBean.getCurrentUser() != null) {
            studentOrders = orderDAO.getOrdersByStudent(authBean.getCurrentUser().getId());
        } else {
            studentOrders = new ArrayList<>();
        }
    }

    public void cancelOrder(int orderId) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (authBean == null || authBean.getCurrentUser() == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unauthorized", "Please log in first."));
            return;
        }

        boolean success = orderDAO.cancelOrder(orderId, authBean.getCurrentUser().getId());
        if (success) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Cancelled", "Your order has been cancelled successfully."));
            refreshOrders();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Cannot Cancel", "Order can only be cancelled while status is 'PLACED'."));
        }
    }

    public void viewOrderDetails(Order order) {
        this.selectedOrder = order;
    }

    public int getStatusStep(String status) {
        if (status == null) return 0;
        switch (status.toUpperCase()) {
            case "PLACED":
                return 1;
            case "PREPARING":
                return 2;
            case "READY":
                return 3;
            case "COLLECTED":
                return 4;
            default:
                return 0; // CANCELLED
        }
    }

    // Getters and Setters
    public AuthBean getAuthBean() { return authBean; }
    public void setAuthBean(AuthBean authBean) { this.authBean = authBean; }

    public List<Order> getStudentOrders() { return studentOrders; }
    public void setStudentOrders(List<Order> studentOrders) { this.studentOrders = studentOrders; }

    public Order getSelectedOrder() { return selectedOrder; }
    public void setSelectedOrder(Order selectedOrder) { this.selectedOrder = selectedOrder; }
}
