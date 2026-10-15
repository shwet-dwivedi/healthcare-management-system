-- Online Healthcare Management System — Database Schema

CREATE DATABASE IF NOT EXISTS healthcare_db;
USE healthcare_db;

-- One table for all roles; doctors/patients hold role-specific extra columns.
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN','DOCTOR','PATIENT') NOT NULL,
    phone         VARCHAR(15),
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hospitals (
    hospital_id         INT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(150) NOT NULL,
    address             VARCHAR(255),
    city                VARCHAR(80),
    latitude            DECIMAL(9,6) NOT NULL,
    longitude           DECIMAL(9,6) NOT NULL,
    phone               VARCHAR(15),
    emergency_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE doctors (
    doctor_id      INT PRIMARY KEY,           -- same value as the matching users.user_id
    hospital_id    INT,
    specialization VARCHAR(80),
    qualification  VARCHAR(120),
    consult_fee    DECIMAL(8,2),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(hospital_id)
);

CREATE TABLE patients (
    patient_id    INT PRIMARY KEY,            -- same value as the matching users.user_id
    date_of_birth DATE,
    gender        ENUM('M','F','OTHER'),
    blood_group   VARCHAR(5),
    address       VARCHAR(255),
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- When a doctor is free to be booked.
CREATE TABLE doctor_availability (
    slot_id    INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id  INT NOT NULL,
    slot_date  DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time   TIME NOT NULL,
    is_booked  BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    UNIQUE KEY uniq_slot (doctor_id, slot_date, start_time)
);

CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id     INT NOT NULL,
    doctor_id      INT NOT NULL,
    slot_id        INT NOT NULL,
    status         ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED') DEFAULT 'PENDING',
    reason         VARCHAR(255),
    booked_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id)  REFERENCES doctors(doctor_id),
    FOREIGN KEY (slot_id)    REFERENCES doctor_availability(slot_id)
);

CREATE TABLE medical_records (
    record_id      INT AUTO_INCREMENT PRIMARY KEY,
    patient_id     INT NOT NULL,
    doctor_id      INT NOT NULL,
    appointment_id INT,
    diagnosis      TEXT,
    prescription   TEXT,
    notes          TEXT,
    record_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id)     REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id)      REFERENCES doctors(doctor_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

CREATE TABLE feedback (
    feedback_id    INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE,
    rating         TINYINT CHECK (rating BETWEEN 1 AND 5),
    comments       VARCHAR(500),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

SHOW TABLES;

select * from appointments;
