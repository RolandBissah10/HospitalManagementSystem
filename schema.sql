-- Hospital Management System Database Schema
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- Departments
CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL
);

-- Patients
CREATE TABLE patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100)
);

-- Doctors
CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialty VARCHAR(100),
    department_id INT,
    phone VARCHAR(20),
    email VARCHAR(100),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Appointments
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'scheduled',
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- Prescriptions
CREATE TABLE prescriptions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    prescription_date DATE NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- Prescription Items
CREATE TABLE prescription_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    prescription_id INT NOT NULL,
    medication VARCHAR(100) NOT NULL,
    dosage VARCHAR(50),
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE
);

-- Patient Feedback
CREATE TABLE patient_feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    appointment_id INT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comments TEXT,
    feedback_date DATETIME NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
);

-- Medical Inventory
CREATE TABLE medical_inventory (
    id INT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    unit VARCHAR(20)
);

-- Prescriptions
ALTER TABLE prescriptions
    ADD COLUMN diagnosis TEXT,
    ADD COLUMN notes TEXT;

-- Update existing prescriptions to have empty values for new columns
UPDATE prescriptions SET diagnosis = '', notes = '' WHERE diagnosis IS NULL;

-- Add with CASCADE
ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE;

ALTER TABLE prescriptions
    ADD CONSTRAINT fk_prescriptions_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE;

ALTER TABLE patient_feedback
    ADD CONSTRAINT fk_patient_feedback_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE;


-- Indexes
CREATE INDEX idx_patients_name ON patients (first_name, last_name);
CREATE INDEX idx_doctors_department ON doctors (department_id);
CREATE INDEX idx_appointments_date ON appointments (appointment_date);
CREATE INDEX idx_appointments_patient ON appointments (patient_id);
CREATE INDEX idx_appointments_doctor ON appointments (doctor_id);
CREATE INDEX idx_prescriptions_patient ON prescriptions (patient_id);
CREATE INDEX idx_prescriptions_doctor ON prescriptions (doctor_id);
CREATE INDEX idx_inventory_name ON medical_inventory (item_name);

-- Sample Data
INSERT INTO departments (name) VALUES ('Cardiology'), ('Neurology'), ('Orthopedics');

INSERT INTO patients (first_name, last_name, date_of_birth, address, phone, email) VALUES
('John', 'Doe', '1980-01-01', '123 Main St', '123-456-7890', 'john.doe@example.com'),
('Jane', 'Smith', '1990-02-02', '456 Elm St', '987-654-3210', 'jane.smith@example.com');

INSERT INTO doctors (first_name, last_name, specialty, department_id, phone, email) VALUES
('Dr. Alice', 'Johnson', 'Cardiologist', 1, '111-222-3333', 'alice.johnson@hospital.com'),
('Dr. Bob', 'Williams', 'Neurologist', 2, '444-555-6666', 'bob.williams@hospital.com');

INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time) VALUES
(1, 1, '2025-12-30', '10:00:00'),
(2, 2, '2025-12-31', '11:00:00');

INSERT INTO prescriptions (patient_id, doctor_id, prescription_date) VALUES
(1, 1, '2025-12-27');

INSERT INTO prescription_items (prescription_id, medication, dosage) VALUES
(1, 'Aspirin', '100mg daily');

INSERT INTO patient_feedback (patient_id, rating, comments, feedback_date) VALUES
(1, 5, 'Excellent service', '2025-12-27');

INSERT INTO medical_inventory (item_name, quantity, unit) VALUES
('Bandages', 100, 'pieces'),
('Syringes', 200, 'pieces');