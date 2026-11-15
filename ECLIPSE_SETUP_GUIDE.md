# Step-by-Step Eclipse IDE Setup Guide

This guide walks you through importing and running the **Multi-Vendor Campus Canteen & Pre-Ordering System** in **Eclipse IDE for Enterprise Java and Web Developers** with **Apache Tomcat** and **MySQL**.

---

## Step 1: Import Database into MySQL

Since MySQL is already installed on your machine (`C:\Program Files\MySQL\MySQL Server 8.0`):

1. Open **MySQL Workbench** (or MySQL 8.0 Command Line Client).
2. Connect to your local MySQL server (default port `3306`).
3. Click **File** &rarr; **Open SQL Script...**
4. Select the file:
   ```
   C:\Users\Chithritha\OneDrive\Desktop\jsf project\database.sql
   ```
5. Click the ⚡ **Execute** (lightning bolt) button to run the entire script.
6. Verify that `campus_canteen_db` appears in your schemas list with 5 tables:
   - `users`
   - `vendors`
   - `menu_items`
   - `orders`
   - `order_items`

---

## Step 2: Configure Database Password

Open [`src/main/resources/db.properties`](file:///c:/Users/Chithritha/OneDrive/Desktop/jsf%20project/src/main/resources/db.properties):
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/campus_canteen_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=root
```
*Note: If your local MySQL root password is not `root`, replace it with your actual password (e.g. `admin`, `password`, or leave empty if no password).*

---

## Step 3: Open and Import into Eclipse

1. Launch **Eclipse IDE for Enterprise Java and Web Developers**.
2. Choose your default workspace (e.g. `C:\Users\Chithritha\eclipse-workspace`).
3. In the top menu, go to:
   **File** &rarr; **Import...**
4. Expand the **Maven** folder and select:
   **Existing Maven Projects** &rarr; Click **Next**.
5. Click **Browse...** next to *Root Directory* and choose:
   ```
   C:\Users\Chithritha\OneDrive\Desktop\jsf project
   ```
6. Ensure `/pom.xml` is checked in the Projects list.
7. Click **Finish**. Eclipse will import the project and resolve all JSF and MySQL libraries automatically.

---

## Step 4: Configure Apache Tomcat in Eclipse

If you don't already have Apache Tomcat configured in Eclipse:

1. Open the **Servers** tab at the bottom of Eclipse (if not visible, go to **Window** &rarr; **Show View** &rarr; **Servers**).
2. Right-click inside the blank Servers window &rarr; **New** &rarr; **Server**.
3. Expand **Apache** and select:
   - **Tomcat v9.0 Server** (or your installed Tomcat version) &rarr; Click **Next**.
4. If prompted for the Tomcat installation directory, click **Browse** and point to your Tomcat folder (e.g. `C:\apache-tomcat-9.0.x`).
5. In the **Add and Remove...** dialog:
   - Select `campus-canteen-jsf` from the left side.
   - Click **Add >** so it moves to *Configured*.
   - Click **Finish**.

---

## Step 5: Run the Application

1. Right-click on the project `campus-canteen-jsf` in the **Project Explorer**.
2. Select **Run As** &rarr; **Run on Server**.
3. Choose your configured Tomcat server and click **Finish**.
4. Eclipse will start Tomcat and open the internal browser at:
   ```
   http://localhost:8080/campus-canteen/
   ```
5. You can also open Google Chrome or any browser and visit:
   ```
   http://localhost:8080/campus-canteen/index.xhtml
   ```

---

## Step 6: Testing All Features

1. **Student Flow:**
   - Log in with `chithritha` / `student123`.
   - Go to **Canteen Menu**, choose a stall (e.g. *South Spices* or *Campus Bites*).
   - Add items to your tray, click **Review Pre-Order Tray**, enter any special notes, and click **Confirm & Place Pre-Order**.
   - Note the generated Token Number (e.g. `ORD-20261115-001`).
   - Click **My Orders** to see the 4-step progress tracker.

2. **Vendor Flow:**
   - Log out and log in with vendor username `southspice` / `vendor123`.
   - You will land directly in **Kitchen Orders**.
   - See the student's order with status `PLACED`.
   - Click **Start Preparing** &rarr; Order updates to `PREPARING`.
   - Click **Mark as Ready for Pickup** &rarr; Order updates to `READY`.
   - Click **Mark as Collected** &rarr; Order updates to `COLLECTED`.
   - Click **Manage Menu** to add new food items or toggle *In Stock / Sold Out*.

3. **Admin Flow:**
   - Log in with `admin` / `admin123`.
   - View overall canteen metrics, all active stalls, registered students, and total revenue.

---

## Troubleshooting Common Issues

- **Port 8080 already in use:**
  - Double click Tomcat in the Servers view.
  - In the Overview page, change HTTP port to `8081` or `8085`.
- **Database Connection Error (Communications link failure):**
  - Make sure MySQL Service is running (`services.msc` &rarr; MySQL80).
  - Check the password in `src/main/resources/db.properties`.
- **Maven Project has a red exclamation mark:**
  - Right-click project &rarr; **Maven** &rarr; **Update Project...** &rarr; Check *Force Update of Snapshots/Releases* &rarr; Click **OK**.
