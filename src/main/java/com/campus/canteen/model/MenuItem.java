package com.campus.canteen.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int vendorId;
    private String vendorName;
    private String stallCounter;
    private String itemName;
    private String category; // "Breakfast", "Main Course", "Snacks", "Beverages", "Dessert"
    private String description;
    private BigDecimal price;
    private boolean available;
    private int preparationTimeMins;
    private Timestamp createdAt;

    public MenuItem() {
        this.available = true;
        this.preparationTimeMins = 10;
        this.price = BigDecimal.ZERO;
    }

    public MenuItem(int id, int vendorId, String itemName, String category, String description, BigDecimal price, boolean available, int preparationTimeMins) {
        this.id = id;
        this.vendorId = vendorId;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.available = available;
        this.preparationTimeMins = preparationTimeMins;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getStallCounter() {
        return stallCounter;
    }

    public void setStallCounter(String stallCounter) {
        this.stallCounter = stallCounter;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getPreparationTimeMins() {
        return preparationTimeMins;
    }

    public void setPreparationTimeMins(int preparationTimeMins) {
        this.preparationTimeMins = preparationTimeMins;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
