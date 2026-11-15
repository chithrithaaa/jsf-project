# Multi-Vendor Campus Canteen & Pre-Ordering System

A full-featured **JavaServer Faces (JSF)** web application developed for college campus canteens. It enables students to browse menus across multiple food stalls, pre-order meals to bypass long lunch lines, and track food preparation progress live. Canteen vendors can manage menus, update prices, toggle item availability, and progress orders from **Placed &rarr; Preparing &rarr; Ready &rarr; Collected**.

---

## 🛠 Tech Stack

- **Frontend:** JavaServer Faces (JSF 2.3 / Mojarra Facelets `.xhtml`), HTML5, CSS3, Bootstrap 5, Bootstrap Icons
- **Backend:** Java (Java SE 8 / 11 / 17), Managed Beans (`@ManagedBean`, `@SessionScoped`, `@ViewScoped`), DAO Pattern
- **Database:** MySQL (Relational Schema with Foreign Keys & Transaction Management)
- **Data Access:** JDBC with PreparedStatements and Transaction Rollback
- **Build Tool / IDE:** Maven (`pom.xml`), Eclipse IDE for Enterprise Java and Web Developers
- **Server:** Apache Tomcat (v8.5, v9.0, or v10 with Jakarta/TomEE)

---

## 🚀 Key Features

### 1. Student Portal
- **Registration & Authentication:** Sign up with student ID/email, secure login session.
- **Multi-Vendor Menu Browsing:** Filter food items by canteen stall / counter number, category (*Breakfast, Main Course, Snacks, Beverages, Dessert*), or keyword search.
- **Food Tray & Pre-Ordering:** Add items from stalls into a virtual tray, customize quantities, add special instructions (e.g. *"less spicy"*), and place orders.
- **Live Order Progress Tracker:** Real-time visual 4-step progress bar:
  $$\text{Placed} \longrightarrow \text{Preparing} \longrightarrow \text{Ready for Pickup} \longrightarrow \text{Collected}$$
- **Order Cancellation:** Students can cancel any order while it is still in the `PLACED` stage.

### 2. Vendor / Kitchen Counter Portal
- **Live Orders Queue:** Immediate view of incoming student pre-orders with customer names and items.
- **One-Click Order Status Updates:**
  - `Placed` &rarr; Click **"Start Preparing"**
  - `Preparing` &rarr; Click **"Mark as Ready for Pickup"**
  - `Ready` &rarr; Click **"Mark as Collected"**
- **Menu Management (CRUD):**
  - Add new menu items with price, prep time, and category.
  - Edit existing items and prices.
  - One-click toggle availability (*In Stock* vs *Sold Out*).
  - Delete items.
- **Stall Metrics:** View active counts for placed, preparing, ready orders, and stall total revenue.

### 3. Administrator Portal
- Overview of all campus stalls, registered students, and total revenue.
- Master log of all campus food orders across all counters.

---

## 🗄️ Database Setup (MySQL)

1. Open **MySQL Workbench** or MySQL Command Line Client.
2. Open and execute the script provided in [`database.sql`](file:///c:/Users/Chithritha/OneDrive/Desktop/jsf%20project/database.sql).
3. The script will automatically:
   - Create database `campus_canteen_db`.
   - Create tables: `users`, `vendors`, `menu_items`, `orders`, and `order_items`.
   - Insert pre-populated sample vendors, menu items, student accounts, and test orders.
4. Verify or change your database credentials in:
   [`src/main/resources/db.properties`](file:///c:/Users/Chithritha/OneDrive/Desktop/jsf%20project/src/main/resources/db.properties):
   ```properties
   db.url=jdbc:mysql://localhost:3306/campus_canteen_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=root
   ```

---

## 🔑 Pre-Configured Test Accounts

| Role | Username | Password | Notes |
|---|---|---|---|
| **Student** | `chithritha` | `student123` | Registered student account |
| **Student** | `student1` | `student123` | Rahul Sharma (Has existing orders) |
| **Vendor (Stall 1)** | `southspice` | `vendor123` | South Spices & Dosa Point (Counter #1) |
| **Vendor (Stall 2)** | `campusbites` | `vendor123` | Campus Bites Burger & Pizza (Counter #2) |
| **Vendor (Stall 3)** | `freshjuice` | `vendor123` | Green Sip Juice & Shake Bar (Counter #3) |
| **Vendor (Stall 4)** | `snackhub` | `vendor123` | Chai & Desi Snack Hub (Counter #4) |
| **Admin** | `admin` | `admin123` | Campus Master Administrator |

---

## 💻 How to Run in Eclipse IDE

For detailed screenshots and steps, see [`ECLIPSE_SETUP_GUIDE.md`](file:///c:/Users/Chithritha/OneDrive/Desktop/jsf%20project/ECLIPSE_SETUP_GUIDE.md).

### Quick Steps:
1. Open **Eclipse IDE for Enterprise Java and Web Developers**.
2. Click **File** &rarr; **Import...** &rarr; **Maven** &rarr; **Existing Maven Projects**.
3. Browse to this folder:
   `C:\Users\Chithritha\OneDrive\Desktop\jsf project`
4. Click **Finish**. Eclipse will import and configure the project.
5. In the **Servers** tab at the bottom:
   - Right click &rarr; **New** &rarr; **Server**.
   - Select **Apache Tomcat v9.0** (or your installed Tomcat version).
   - Add `campus-canteen-jsf` to configured resources &rarr; **Finish**.
6. Right-click the project &rarr; **Run As** &rarr; **Run on Server**.
7. Open your browser and navigate to:
   ```
   http://localhost:8080/campus-canteen/
   ```

---

## 🎓 Lab Viva Questions & Answers

1. **Q: What is JavaServer Faces (JSF)?**
   - **A:** JSF is a server-side component-based web framework for Java EE/Jakarta EE. It simplifies web UI development using reusable Facelets (`.xhtml`) components, event handling, and Managed Beans.
2. **Q: How does the order status progression work in this system?**
   - **A:** When a student checks out, the order is stored with status `PLACED`. The vendor sees it in their live dashboard and clicks *"Start Preparing"* (`PREPARING`). When food is cooked, vendor clicks *"Mark as Ready"* (`READY`). When the student collects it at Counter #1, it is marked `COLLECTED`.
3. **Q: How is database consistency maintained during order placement?**
   - **A:** `OrderDAO.placeOrder()` uses JDBC Transactions (`conn.setAutoCommit(false)`). It inserts into the `orders` table and batches items into `order_items`. If any operation fails, it performs `conn.rollback()` so partial orders are never saved.
