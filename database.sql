-- ====================================================================
-- Multi-Vendor Campus Canteen & Pre-Ordering System
-- Database Schema & Sample Data for MySQL
-- Compatible with MySQL 5.7, 8.0+
-- ====================================================================

-- 1. Create and switch to database
CREATE DATABASE IF NOT EXISTS campus_canteen_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_canteen_db;

-- 2. Drop existing tables if needed (in reverse order of dependencies)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS vendors;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- 3. Users Table (Students, Vendors, Admins)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    role ENUM('STUDENT', 'VENDOR', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. Vendors Table (Canteen Stalls)
CREATE TABLE vendors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    stall_name VARCHAR(100) NOT NULL,
    counter_number VARCHAR(20) NOT NULL,
    cuisine_type VARCHAR(60) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vendor_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Menu Items Table
CREATE TABLE menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    category ENUM('Breakfast', 'Main Course', 'Snacks', 'Beverages', 'Dessert') NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    preparation_time_mins INT NOT NULL DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Orders Table
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(40) NOT NULL UNIQUE,
    student_id INT NOT NULL,
    vendor_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PLACED', 'PREPARING', 'READY', 'COLLECTED', 'CANCELLED') NOT NULL DEFAULT 'PLACED',
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pickup_time TIMESTAMP NULL,
    notes VARCHAR(255),
    CONSTRAINT fk_order_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Order Items Table
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_menu FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ====================================================================
-- SAMPLE DATA INSERTION
-- ====================================================================

-- Users: Admin, Vendors, and Students
INSERT INTO users (id, username, password, full_name, email, phone, role) VALUES
(1, 'admin', 'admin123', 'Campus Admin', 'admin@canteen.edu', '9876543210', 'ADMIN'),
(2, 'southspice', 'vendor123', 'Murugan Swamy', 'southspice@canteen.edu', '9876543211', 'VENDOR'),
(3, 'campusbites', 'vendor123', 'Anil Kumar', 'campusbites@canteen.edu', '9876543212', 'VENDOR'),
(4, 'freshjuice', 'vendor123', 'Sunita Rao', 'freshjuice@canteen.edu', '9876543213', 'VENDOR'),
(5, 'snackhub', 'vendor123', 'Vikram Singh', 'snackhub@canteen.edu', '9876543214', 'VENDOR'),
(6, 'student1', 'student123', 'Rahul Sharma', 'rahul@student.edu', '9123456780', 'STUDENT'),
(7, 'student2', 'student123', 'Priya Patel', 'priya@student.edu', '9123456781', 'STUDENT'),
(8, 'chithritha', 'student123', 'Chithritha S', 'chithritha@student.edu', '9123456782', 'STUDENT');

-- Canteen Vendor Stalls
INSERT INTO vendors (id, user_id, stall_name, counter_number, cuisine_type, description, is_active) VALUES
(1, 2, 'South Spices & Dosa Point', 'Counter #1', 'South Indian', 'Authentic crispy dosas, fluffy idlis, vada, and filter coffee.', TRUE),
(2, 3, 'Campus Bites Burger & Pizza', 'Counter #2', 'Fast Food & Continental', 'Delicious grilled burgers, freshly baked mini pizzas, and french fries.', TRUE),
(3, 4, 'Green Sip Juice & Shake Bar', 'Counter #3', 'Beverages & Healthy', 'Fresh seasonal juices, milkshakes, smoothies, and fruit bowls.', TRUE),
(4, 5, 'Chai & Desi Snack Hub', 'Counter #4', 'North Indian & Snacks', 'Hot samosas, kachori, paneer rolls, and masala ginger tea.', TRUE);

-- Menu Items
INSERT INTO menu_items (vendor_id, item_name, category, description, price, is_available, preparation_time_mins) VALUES
-- South Spices (Vendor 1)
(1, 'Masala Dosa', 'Breakfast', 'Crispy fermented crepe stuffed with spiced potato masala and served with chutneys.', 60.00, TRUE, 8),
(1, 'Plain Butter Dosa', 'Breakfast', 'Golden crispy dosa roasted with butter, served with sambar and coconut chutney.', 50.00, TRUE, 6),
(1, 'Steamed Idli (2 Pcs) & Vada (1 Pc)', 'Breakfast', 'Soft steamed rice cakes and crispy lentil fritter with hot sambar.', 45.00, TRUE, 5),
(1, 'South Indian Veg Meals / Thali', 'Main Course', 'Rice, sambar, rasam, curd, 2 veg curries, papad, and payasam.', 90.00, TRUE, 10),
(1, 'Curd Rice with Pomegranate', 'Main Course', 'Comforting tempered yogurt rice topped with mustard seeds and pomegranate.', 50.00, TRUE, 5),
(1, 'Filter Coffee', 'Beverages', 'Traditional South Indian aromatic frothy filter coffee.', 25.00, TRUE, 4),

-- Campus Bites (Vendor 2)
(2, 'Classic Veg Cheese Burger', 'Snacks', 'Soft toasted bun with crispy veg patty, cheddar cheese slice, and mayo.', 70.00, TRUE, 10),
(2, 'Paneer Tikka Burger', 'Snacks', 'Juicy grilled paneer slab seasoned with tandoori spices and spicy chipotle.', 85.00, TRUE, 12),
(2, 'Crispy French Fries (Peri-Peri)', 'Snacks', 'Golden fried potato batons tossed in spicy peri-peri dust.', 55.00, TRUE, 7),
(2, 'Margherita Mini Pizza (7 Inch)', 'Main Course', 'Hand-stretched mini crust topped with herb tomato sauce and mozzarella.', 110.00, TRUE, 15),
(2, 'White Sauce Cheesy Pasta', 'Main Course', 'Penne pasta enveloped in creamy parmesan garlic cheese sauce.', 120.00, TRUE, 12),
(2, 'Choco Lava Cake', 'Dessert', 'Warm chocolate mini cake with molten dark chocolate center.', 50.00, TRUE, 5),

-- Green Sip Juice Bar (Vendor 3)
(3, 'Fresh Watermelon Juice', 'Beverages', 'Refreshing 100% chilled pure watermelon juice without added water.', 40.00, TRUE, 5),
(3, 'Mosambi (Sweet Lime) Juice', 'Beverages', 'Freshly pressed sweet lime juice with a pinch of black salt.', 45.00, TRUE, 5),
(3, 'Oreo Milkshake with Ice Cream', 'Beverages', 'Creamy milkshake blended with crushed Oreos and vanilla ice cream scoop.', 70.00, TRUE, 6),
(3, 'Nutella Brownie Shake', 'Beverages', 'Rich chocolate shake loaded with genuine Nutella and fudge brownie chunks.', 85.00, TRUE, 7),
(3, 'Fresh Seasonal Fruit Salad', 'Dessert', 'Assorted diced fruits: apple, papaya, banana, and pomegranate with honey.', 60.00, TRUE, 5),

-- Chai & Snack Hub (Vendor 4)
(4, 'Punjabi Samosa (2 Pcs)', 'Snacks', 'Flaky pastry filled with spiced potato and green peas, with mint chutney.', 35.00, TRUE, 4),
(4, 'Paneer Kathi Roll', 'Snacks', 'Flaky paratha wrap stuffed with marinated paneer, onions, and sauces.', 75.00, TRUE, 10),
(4, 'Bread Pakoda with Green Chutney', 'Snacks', 'Crispy besan battered spiced potato sandwich fritter.', 30.00, TRUE, 5),
(4, 'Chole Bhature (2 Bhature)', 'Main Course', 'Rich tangy chickpea curry served with two puffed golden bhatures and pickle.', 95.00, TRUE, 12),
(4, 'Special Masala Adrak Chai', 'Beverages', 'Freshly brewed aromatic tea infused with crushed ginger and cardamom.', 20.00, TRUE, 4);

-- Sample Orders for Demonstration
INSERT INTO orders (id, order_number, student_id, vendor_id, total_amount, status, order_time, notes) VALUES
(1, 'ORD-20261115-001', 6, 1, 85.00, 'READY', NOW() - INTERVAL 25 MINUTE, 'Less spicy chutney please'),
(2, 'ORD-20261115-002', 7, 2, 125.00, 'PREPARING', NOW() - INTERVAL 15 MINUTE, 'Extra crispy fries'),
(3, 'ORD-20261115-003', 8, 4, 95.00, 'PLACED', NOW() - INTERVAL 5 MINUTE, 'Serve hot'),
(4, 'ORD-20261115-004', 6, 3, 110.00, 'COLLECTED', NOW() - INTERVAL 60 MINUTE, 'No extra sugar');

-- Order Items
INSERT INTO order_items (order_id, menuItem_id, item_name, unit_price, quantity, subtotal) VALUES
(1, 1, 'Masala Dosa', 60.00, 1, 60.00),
(1, 6, 'Filter Coffee', 25.00, 1, 25.00),

(2, 7, 'Classic Veg Cheese Burger', 70.00, 1, 70.00),
(2, 9, 'Crispy French Fries (Peri-Peri)', 55.00, 1, 55.00),

(3, 19, 'Chole Bhature (2 Bhature)', 95.00, 1, 95.00),

(4, 13, 'Fresh Watermelon Juice', 40.00, 1, 40.00),
(4, 15, 'Oreo Milkshake with Ice Cream', 70.00, 1, 70.00);
