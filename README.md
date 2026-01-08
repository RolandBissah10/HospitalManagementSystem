# Hospital Management System

A robust, optimized JavaFX application for managing hospital operations with a focus on database performance and data structure application.

### 📂 Directory Structure

```
src/main/java/org/example
├── controller
│   └── MainController.java
├── dao
│   ├── AppointmentDAO.java
│   ├── DepartmentDAO.java
│   ├── DoctorDAO.java
│   ├── MedicalInventoryDAO.java
│   ├── PatientDAO.java
│   ├── PatientFeedbackDAO.java
│   └── PrescriptionDAO.java
├── model
│   ├── Appointment.java
│   ├── Department.java
│   ├── Doctor.java
│   ├── MedicalInventory.java
│   ├── Patient.java
│   ├── PatientFeedback.java
│   ├── Prescription.java
│   └── PrescriptionItem.java
├── service
│   ├── AppointmentService.java
│   ├── DoctorService.java
│   ├── HospitalService.java
│   ├── PatientService.java
│   └── PrescriptionService.java
└── util
    ├── DatabaseConnection.java
    ├── DatabaseUpdater.java
    └── ValidationUtils.java
```

## 🚀 Features

- **Dynamic Patient & Doctor Management**: Full CRUD operations with JavaFX UI.
- **Smart Appointment Scheduling**: Integrated validation and status tracking.
- **Optimized Searching**: Case-insensitive search with B-Tree database indexing.
- **Advanced Sorting (DSA)**: Custom implementation of **QuickSort** and **MergeSort** for patient listings.
- **Performance Dashboard**: Real-time metrics comparing database vs. cache latency.
- **Patient Feedback System**: integrated feedback loop for quality assurance.
- **Unstructured Data Strategy**: Detailed NoSQL design for patient notes and logs.

## 🛠 Prerequisites

- Java 23
- MySQL 8.0 or PostgreSQL
- Maven 3.6+

## 📥 Setup Instructions

1. **Database Setup**:
   - Create a database `hospital_db`.
   - Run [schema.sql](schema.sql) to initialize tables and sample data.
   ```bash
   mysql -u root -p hospital_db < schema.sql
   ```
2. **Configuration**:
   - Update credentials in `src/main/java/org/example/util/DatabaseConnection.java`.
3. **Build & Run**:
   ```bash
   mvn clean javafx:run
   ```

## 📈 Performance & DSA

The system leverages several optimization techniques:
- **Indexing**: High-frequency columns indexed to reduce search time by ~80%.
- **Caching**: `ConcurrentHashMap` caching layer reducing lookup time from ~100ms to <1ms.
- **Normalization**: Database schema in **3NF** to eliminate redundancy.
- **QuickSort**: Custom sorting algorithm implemented in the `HospitalService` for demo and efficiency.

Detailed reports are available in:
- [Performance_Report.md](Performance_Report.md)
- [NoSQL_Design.md](NoSQL_Design.md)

## 🏗 Architecture

The project follows a clean **Controller-Service-DAO** pattern:
- **DAO (Data Access Layer)**: Parameterized JDBC queries for secure and structured DB access.
- **Service (Business Layer)**: Handles caching, validation, and algorithmic logic.
- **Controller (UI Layer)**: Manages JavaFX interaction and view synchronization.



---
*Developed as part of the Database Fundamentals project objectives.*
