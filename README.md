# ORDER AND DELIVERY MANAGEMENT SYSTEM

>This is a simple order and delivery management system built using Springboot and Reactjs. It allows users to place orders, manage deliveries, and track the status of their orders.

## 🚀 Getting Started

1. **Clone this repository**

   ```bash
   git clone https://github.com/QuanNM210803/order-delivery-ms.git
   ```

2. **Clone this repository**

   ```bash
   In the notification-service configuration file, add your Brevo SMTP-key to be able to send mail.
   Link to get free SMTP-key: https://app.brevo.com/
   ```
   
3. **Run with Docker Compose**

   ```bash
   docker-compose up --build
   ```

4. **Access Application**

   ```bash
   http://localhost:3000
   ```

5. **Login**

   ```bash
   Account customer: customer/123456
   Account admin: admin/123456
   Account delivery staff: deliverystaff/123456
   ```
---

## 🚦 Features

- `Order Tracking`  
  Track your order status through various states: `CREATED`, `ASSIGNED`, `PICKED_UP`, `IN_TRANSIT`, `DELIVERED`, `COMPLETED`, or `CANCELLED`.

- `Shipping Fee Estimation`  
  Estimate delivery costs based on sender and receiver locations before placing an order.

- `Customer Order Creation`  
  Customers can create delivery orders by filling in pickup/delivery addresses, package details.

- `Admin Assign Orders`  
  Admins can view unassigned orders and allocate them to available delivery staff based on zones or workload.

- `User Management`  
  Admins can manage all user accounts in the system, including customers, delivery staff, and other admins: view, update, or disable accounts.

- `Delivery Staff View Assigned Orders`  
  Delivery personnel can log in to see all orders assigned to them with full pickup and drop-off information.

- `Revenue Statistics`  
  View detailed performance metrics and income reports by delivery staff and administrators over a customizable timeframe.

- `Email Notifications`  
  Users receive email alerts for important events: order creation, status updates, delivery confirmations, and more.

---

## 🧠 System Overview

- `auth-service`:  
  Handles user authentication and authorization (login, register, role-based access control).

- `delivery-service`:  
  Stores and updates delivery status reported by delivery staff during the shipping process.

- `notification-service`:  
  Sends email notifications to users regarding order status updates and other system events.

- `order-service`:  
  Manages customer orders, including order creation and processing. Handles cost estimation and delivery distance calculations.

- `tracking-service`:  
  Optimized for querying detailed order and delivery information efficiently, supporting tracking and monitoring use cases.

## Application
1. `Login View`
   ![](./asset/login.jpg)
2. `Register View`
   ![](./asset/register.jpg)
3. `Look up orders View`
   ![](./asset/tra-cuu.jpg)
4. `Estimated shipping cost View`
   ![](./asset/uoc-luong-phi.jpg)
5. `Create order View`
   ![](./asset/tao-don-1.jpg)
   ![](./asset/tao-don-2.jpg)
6. `Order details View`
   ![](./asset/chi-tiet-don-hang.jpg)
7. `Admin View`
   ![](./asset/danh-sach-don.jpg)
    ![](./asset/danh-sach-nguoi-dung.jpg)
8. `Statistics View`
   ![](./asset/thong-ke-tai-xe.jpg)
> The project is under development. 