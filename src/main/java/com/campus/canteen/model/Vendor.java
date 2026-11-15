package com.campus.canteen.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Vendor implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String stallName;
    private String counterNumber;
    private String cuisineType;
    private String description;
    private boolean active;
    private Timestamp createdAt;

    // Additional helper fields
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;

    public Vendor() {
    }

    public Vendor(int id, int userId, String stallName, String counterNumber, String cuisineType, String description, boolean active) {
        this.id = id;
        this.userId = userId;
        this.stallName = stallName;
        this.counterNumber = counterNumber;
        this.cuisineType = cuisineType;
        this.description = description;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public String getCounterNumber() {
        return counterNumber;
    }

    public void setCounterNumber(String counterNumber) {
        this.counterNumber = counterNumber;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
}
