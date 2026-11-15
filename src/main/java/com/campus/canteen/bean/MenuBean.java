package com.campus.canteen.bean;

import com.campus.canteen.dao.MenuItemDAO;
import com.campus.canteen.dao.VendorDAO;
import com.campus.canteen.model.MenuItem;
import com.campus.canteen.model.Vendor;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "menuBean")
@ViewScoped
public class MenuBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private MenuItemDAO menuItemDAO;
    private VendorDAO vendorDAO;

    private List<MenuItem> menuItems;
    private List<Vendor> activeVendors;

    private Integer selectedVendorId;
    private String selectedCategory = "ALL";
    private String searchKeyword;

    @PostConstruct
    public void init() {
        menuItemDAO = new MenuItemDAO();
        vendorDAO = new VendorDAO();
        loadVendors();
        filterMenu();
    }

    public void loadVendors() {
        activeVendors = vendorDAO.findAllActiveVendors();
    }

    public void filterMenu() {
        if ((selectedVendorId == null || selectedVendorId == 0) &&
            (selectedCategory == null || "ALL".equalsIgnoreCase(selectedCategory)) &&
            (searchKeyword == null || searchKeyword.trim().isEmpty())) {
            menuItems = menuItemDAO.findAllAvailable();
        } else {
            menuItems = menuItemDAO.filter(selectedVendorId, selectedCategory, searchKeyword);
        }
    }

    public void resetFilter() {
        this.selectedVendorId = null;
        this.selectedCategory = "ALL";
        this.searchKeyword = null;
        filterMenu();
    }

    public void selectCategory(String category) {
        this.selectedCategory = category;
        filterMenu();
    }

    public void selectVendor(Integer vendorId) {
        this.selectedVendorId = vendorId;
        filterMenu();
    }

    // Getters and Setters
    public List<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }

    public List<Vendor> getActiveVendors() { return activeVendors; }
    public void setActiveVendors(List<Vendor> activeVendors) { this.activeVendors = activeVendors; }

    public Integer getSelectedVendorId() { return selectedVendorId; }
    public void setSelectedVendorId(Integer selectedVendorId) { this.selectedVendorId = selectedVendorId; }

    public String getSelectedCategory() { return selectedCategory; }
    public void setSelectedCategory(String selectedCategory) { this.selectedCategory = selectedCategory; }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }
}
