package com.campus.canteen.bean;

import com.campus.canteen.dao.MenuItemDAO;
import com.campus.canteen.dao.OrderDAO;
import com.campus.canteen.dao.UserDAO;
import com.campus.canteen.dao.VendorDAO;
import com.campus.canteen.model.Order;
import com.campus.canteen.model.User;
import com.campus.canteen.model.Vendor;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean(name = "adminBean")
@ViewScoped
public class AdminBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;
    private VendorDAO vendorDAO;
    private MenuItemDAO menuItemDAO;
    private OrderDAO orderDAO;

    private int totalStudents;
    private int totalVendors;
    private int totalMenuItems;
    private int totalOrders;
    private BigDecimal totalRevenue;

    private List<Vendor> allVendors;
    private List<User> allStudents;
    private List<Order> allOrders;

    @PostConstruct
    public void init() {
        userDAO = new UserDAO();
        vendorDAO = new VendorDAO();
        menuItemDAO = new MenuItemDAO();
        orderDAO = new OrderDAO();
        loadDashboardData();
    }

    public void loadDashboardData() {
        totalStudents = userDAO.countStudents();
        totalVendors = vendorDAO.countVendors();
        totalMenuItems = menuItemDAO.countTotalItems();
        totalRevenue = orderDAO.getTotalSalesRevenue();

        allVendors = vendorDAO.findAllVendors();
        allStudents = userDAO.findAllStudents();
        allOrders = orderDAO.getAllOrders();
        totalOrders = allOrders != null ? allOrders.size() : 0;
    }

    // Getters and Setters
    public int getTotalStudents() { return totalStudents; }
    public int getTotalVendors() { return totalVendors; }
    public int getTotalMenuItems() { return totalMenuItems; }
    public int getTotalOrders() { return totalOrders; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }

    public List<Vendor> getAllVendors() { return allVendors; }
    public List<User> getAllStudents() { return allStudents; }
    public List<Order> getAllOrders() { return allOrders; }
}
