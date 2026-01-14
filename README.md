# Hospital Management System

A robust, optimized JavaFX application for managing hospital operations with a focus on database performance, data structure application, and microservices architecture.

### 📂 Directory Structure

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
│   ├── PatientDAO.java
│   ├── PatientFeedbackDAO.java
│   └── PrescriptionDAO.java
├── model                       # Data Models
│   ├── Appointment.java
│   ├── Department.java
│   ├── Doctor.java
│   ├── MedicalInventory.java
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
    ├── DatabaseConnection.java
    ├── DatabaseUpdater.java
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

- **Microservices Architecture**: Monolithic controller refactored into domain-specific controllers (e.g., `DoctorController`, `PatientController`) for better maintainability.
- **Role-Based Portals**: Dedicated dashboards for Administrators, Doctors, Receptionists, and Patients.
- **Dynamic Patient & Doctor Management**: Full CRUD operations with JavaFX UI.
- **Smart Appointment Scheduling**: Integrated validation and status tracking.
- **Optimized Searching**: Case-insensitive search with B-Tree database indexing.
- **Advanced Sorting (DSA)**: Custom implementation of **QuickSort** and **MergeSort** for patient listings.
- **Performance Dashboard**: Real-time metrics comparing database vs. cache latency.
- **Patient Feedback System**: Integrated feedback loop for quality assurance.
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

The project follows a clean **Controller-Service-DAO** pattern refactored into a modular design:
- **DAO (Data Access Layer)**: Parameterized JDBC queries for secure and structured DB access.
- **Service (Business Layer)**: Handles caching, validation, and algorithmic logic.
- **Controller (UI Layer)**:
    - **View Controllers**: Handle FXML layouts and event delegation (e.g., `AdministratorController`).
    - **Logic Controllers**: Handle specific business logic (e.g., `PatientController`), keeping classes small and focused.


---
*Developed as part of the Database Fundamentals project objectives.*
