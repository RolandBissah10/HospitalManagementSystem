# Hospital Management System

A comprehensive JavaFX application for managing hospital operations including patients, doctors, appointments, prescriptions, and inventory.

## Features

- Patient management (CRUD operations)
- Doctor management
- Appointment scheduling
- Prescription management
- Medical inventory tracking
- Patient feedback system
- Search and sorting capabilities
- In-memory caching for performance

## Prerequisites

- Java 23
- MySQL 8.0
- Maven 3.6+

## Setup

1. Install MySQL and create a database named `hospital_db`.

2. Run the SQL script to create tables and insert sample data:
   ```bash
   mysql -u root -p hospital_db < schema.sql
   ```

3. Update database credentials in `src/main/java/org/example/util/DatabaseConnection.java`.

4. Build the project:
   ```bash
   mvn clean compile
   ```

5. Run the application:
   ```bash
   mvn javafx:run
   ```

## Database Schema

The system uses a normalized relational database with the following entities:
- Patients
- Doctors
- Departments
- Appointments
- Prescriptions
- PrescriptionItems
- PatientFeedback
- MedicalInventory

## Architecture

- **Model**: Entity classes
- **DAO**: Data Access Objects for database operations
- **Service**: Business logic layer
- **Controller**: JavaFX controllers
- **UI**: FXML-based user interface

## Performance Optimizations

- Database indexes on frequently searched columns
- In-memory caching using HashMap
- Parameterized queries to prevent SQL injection
- Efficient sorting and searching algorithms

## Testing

Run tests with:
```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to branch
5. Create a Pull Request
