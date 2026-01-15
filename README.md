# Hospital Management System

A robust, optimized JavaFX application for managing hospital operations with a **hybrid database architecture** (MySQL + MongoDB), microservices design, and advanced data structure implementation.

## 📂 Directory Structure

```
src/main/java/org/example
├── controller                  # Domain-specific & View Controllers
│   ├── AdministratorController.java
│   ├── AppointmentController.java
│   ├── DepartmentController.java
│   ├── DoctorController.java
│   ├── DoctorPortalController.java
│   ├── FeedbackController.java
│   ├── InventoryController.java
│   ├── MainController.java     # Application entry/initializer
│   ├── MedicalLogController.java    # NoSQL medical logs
│   ├── PatientController.java
│   ├── PatientPortalController.java
│   ├── PrescriptionController.java
│   ├── ReceptionistController.java
│   └── ReportController.java
├── dao                         # Data Access Objects
│   ├── AppointmentDAO.java
│   ├── DepartmentDAO.java
│   ├── DoctorDAO.java
│   ├── MedicalInventoryDAO.java
│   ├── MedicalLogDAO.java      # MongoDB DAO
│   ├── PatientDAO.java
│   ├── PatientFeedbackDAO.java
│   └── PrescriptionDAO.java
├── model                       # Data Models
│   ├── Appointment.java
│   ├── Department.java
│   ├── Doctor.java
│   ├── MedicalInventory.java
│   ├── MedicalLog.java         # NoSQL model
│   ├── Patient.java
│   ├── PatientFeedback.java
│   ├── Prescription.java
│   └── PrescriptionItem.java
├── service                     # Business Logic Services
│   ├── AppointmentService.java
│   ├── DoctorService.java
│   ├── HospitalService.java
│   ├── PatientService.java
│   └── PrescriptionService.java
└── util                        # Utilities
    ├── AlertUtils.java
    ├── DatabaseConnection.java     # MySQL connection
    ├── DatabaseUpdater.java
    ├── MongoDBConnection.java      # MongoDB Atlas connection
    └── ValidationUtils.java

src/main/resources              # FXML Views & Styles
├── AdministratorView.fxml
├── DoctorView.fxml
├── MainView.fxml
├── PatientView.fxml
├── ReceptionistView.fxml
└── styles.css
```

## 🚀 Features

### Core Functionality
- **Microservices Architecture**: Monolithic controller refactored into domain-specific controllers for better maintainability
- **Role-Based Portals**: Dedicated dashboards for Administrators, Doctors, Receptionists, and Patients
- **Dynamic Patient & Doctor Management**: Full CRUD operations with JavaFX UI
- **Smart Appointment Scheduling**: Integrated validation and status tracking
- **Inventory Management**: Track medical supplies with low-stock alerts
- **Prescription Management**: Digital prescription creation and tracking

### Database & Performance
- **Hybrid Database Architecture**: 
  - **MySQL** for structured relational data (patients, doctors, appointments)
  - **MongoDB Atlas** for unstructured medical logs and notes
- **Optimized Searching**: Case-insensitive search with B-Tree database indexing
- **Advanced Sorting (DSA)**: Custom **QuickSort** implementation for patient listings
- **Performance Dashboard**: Real-time metrics comparing database vs. cache latency
- **Caching**: `ConcurrentHashMap` reducing lookup time from ~100ms to <1ms
- **Normalization**: Database schema in **3NF** to eliminate redundancy

### Additional Features
- **Patient Feedback System**: Integrated feedback loop for quality assurance
- **Department Management**: Organize doctors by departments
- **Medical Logs (NoSQL)**: Store unstructured patient notes in MongoDB

## 🛠 Prerequisites

- **Java 23**
- **MySQL 8.0** or PostgreSQL
- **MongoDB Atlas** account (free tier works)
- **Maven 3.6+**

## 📥 Setup Instructions

### 1. MySQL Database Setup
Create a database and initialize tables:
```bash
mysql -u root -p hospital_db < schema.sql
```

Update MySQL credentials in:
```
src/main/java/org/example/util/DatabaseConnection.java
```

### 2. MongoDB Atlas Setup
1. Create a free MongoDB Atlas cluster at [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas)
2. Get your connection string
3. Update MongoDB credentials in:
```
src/main/java/org/example/util/MongoDBConnection.java
```

Replace the `CONNECTION_STRING` with your Atlas URI:
```java
private static final String CONNECTION_STRING = "mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/...";
```

### 3. Build & Run
```bash
mvn clean javafx:run
```

## 📈 Performance & Data Structures

The system leverages several optimization techniques:

| Technique | Implementation | Performance Gain |
|-----------|---------------|------------------|
| **Indexing** | B-Tree indexes on high-frequency columns | ~80% search time reduction |
| **Caching** | `ConcurrentHashMap` in-memory cache | 100ms → <1ms lookup time |
| **Sorting** | Custom QuickSort algorithm | O(n log n) average case |
| **Normalization** | 3NF database schema | Eliminates data redundancy |
| **NoSQL** | MongoDB for unstructured data | Flexible schema for medical logs |

### Detailed Documentation
- [Performance_Report.md](Performance_Report.md) - Benchmark results and analysis
- [NoSQL_Design.md](NoSQL_Design.md) - MongoDB schema design rationale

## 🏗 Architecture

The project follows a clean **Controller-Service-DAO** pattern with microservices design:

### Layers
1. **DAO (Data Access Layer)**: 
   - Parameterized JDBC queries for MySQL
   - MongoDB driver for NoSQL operations
   - Secure and structured database access

2. **Service (Business Layer)**: 
   - Caching logic
   - Input validation
   - Sorting algorithms
   - Business rules enforcement

3. **Controller (UI Layer)**:
   - **View Controllers**: Handle FXML layouts and event delegation (e.g., `AdministratorController`)
   - **Logic Controllers**: Handle specific business logic (e.g., `PatientController`), keeping classes small and focused (<250 lines)

### Design Principles
- **Single Responsibility**: Each controller handles one domain
- **Separation of Concerns**: Clear boundaries between layers
- **Code Maintainability**: No class exceeds 250 lines (MainController < 50 lines)

## 🎯 Key Technologies

- **Frontend**: JavaFX 23 with FXML
- **Backend**: Java 23
- **Relational DB**: MySQL 8.0 (JDBC)
- **NoSQL DB**: MongoDB Atlas (MongoDB Driver 4.10.1)
- **Build Tool**: Maven
- **Architecture**: MVC + Microservices

## 📝 Usage

### For Administrators
- Full system access
- Manage patients, doctors, appointments, inventory, prescriptions
- View system reports and statistics

### For Doctors
- View appointments and patient records
- Create and manage prescriptions
- Add medical logs (stored in MongoDB)

### For Receptionists
- Register new patients
- Schedule appointments
- Search patient records

### For Patients
- View personal appointments
- Submit feedback

---

*Developed as part of the Database Fundamentals project - demonstrating hybrid database architecture, microservices design, and advanced data structure implementation.*
