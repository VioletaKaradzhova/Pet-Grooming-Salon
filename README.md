# 🐾 Paws & Relax - Salon Management System

**Paws & Relax** is a comprehensive, full-stack web application designed to manage the daily operations of a premium pet grooming salon. Built with Spring Boot and Thymeleaf, the system provides a seamless experience for both clients booking spa days for their pets, and salon staff managing appointments and business logic.

## 🌟 Overview

The application is built around a secure, role-based architecture. It features a modern, responsive user interface utilizing reusable Thymeleaf layout fragments, dynamic database-driven forms, and intuitive user feedback mechanisms (flash messages and confirmation prompts). The codebase strictly adheres to the MVC (Model-View-Controller) design pattern and standard Spring Boot conventions.

### Key Stakeholders:
* **Clients:** Can register, manage their pet profiles, book services, and manage their schedules.
* **Staff:** Can view the master salon schedule and progress appointments through their lifecycle.
* **Management/Admins:** Can oversee all operations and provision new employee accounts.

---

## ✨ Core Functionality

### Client Portal
* **Pet Management:** Add, edit, and safely delete pets. (Includes database integrity safeguards to prevent deleting pets with active appointment histories).
* **Smart Booking:** Book appointments using dynamic dropdowns populated by the user's specific pets and available salon service packages.
* **Schedule Management:** View upcoming appointments, reschedule (edit) dates and services, or cancel bookings with visual confirmation prompts.

### Staff & Admin Dashboard
* **Global Schedule:** A master view of all salon appointments, displaying the pet, owner, service, and time.
* **Lifecycle Management:** Staff can update appointment statuses dynamically (`SCHEDULED` ➔ `IN_PROGRESS` ➔ `COMPLETE`) or cancel them. Business logic restrictions have been implemented to prevent reverting statuses.
* **Access Control:** Admins and Management can create new secure credentials for incoming staff members.

### UI / UX Features
* **Modular Design:** Uses Thymeleaf fragments to render a global navigation bar and footer dynamically across all pages.
* **Smart Navigation:** Automatically hides guest-only marketing links (like Home and About) once a client securely logs in.
* **Visual Feedback:** Implements Spring Boot `RedirectAttributes` to display green success banners after CRUD operations (creates, updates, deletes).
* **Custom Branding:** Features a custom SVG paw-print background pattern, responsive CSS grid layouts, and distinctive "Hero" banners for marketing pages.

---

## 🛠️ Tech Stack
* **Java version:** 17 or higher
* **Spring Boot version:** 3.4.0
* **Build tool:** Maven
* **Backend:** Spring Boot (Web, Data JPA, Validation)
* **Frontend:** HTML5, CSS3, Thymeleaf Templating Engine
* **Database:** Relational Database (MySQL) mapped via Hibernate
* **Session Management:** Jakarta HttpSession

---

## 🚀 Setup & Installation Instructions

### Prerequisites
1. **Java Development Kit (JDK):** Version 17 or higher.
2. **Database:** A running instance of MySQL.
3. **IDE:** IntelliJ IDEA, Eclipse, or VS Code.

### Step-by-Step Installation
1. **Clone the Repository:**
    ```bash
   git clone https://github.com/VioletaKaradzhova/Pet-Grooming-Salon.git
   cd pet-grooming-salon
   
2. **Configure the Database:**

   Open src/main/resources/application.properties and update the connection string and credentials to match your local database instance:

```
spring.datasource.url=jdbc:mysql://localhost:3306/grooming_salon?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

3. **Seed Initial Data:**

   The application features an automated `DataInitializer` class that provisions the database upon its first launch. You do not need to run any manual SQL scripts.

   When you start the application, Spring Boot will automatically generate:
* **The Master Admin Account**
   * **Username:** `admin`
   * **Password:** `admin123`
* **The core Service Packages** (Bath & Brush, Full Grooming Spa, etc.) matching the website's public services menu so that the booking dropdowns function immediately.

4. **Run the Application:**

    Using your IDE, locate the main application class GroomingSalonApplication.java and run it. Alternatively, use the Maven wrapper in your IDE terminal:

```bash
   mvn spring-boot:run
```

5. **Access the Application:**
   Open your web browser and navigate to:

http://localhost:8080