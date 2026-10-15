-- Users
INSERT INTO users (user_id, full_name, email, password_hash, role, phone) VALUES
(1, 'Anita Verma', 'admin@ohms.com', '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'ADMIN', '9900000001'),
(2, 'Dr. Rohan Mehta',  'rohan.mehta@ohms.com',  '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'DOCTOR', '9900000002'),
(3, 'Dr. Priya Rao',    'priya.rao@ohms.com',    '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'DOCTOR', '9900000003'),
(4, 'Dr. Arjun Singh',  'arjun.singh@ohms.com',  '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'DOCTOR', '9900000004'),
(5, 'Dr. Kavita Joshi', 'kavita.joshi@ohms.com', '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'DOCTOR', '9900000005'),
(6, 'Dr. Suresh Nair',  'suresh.nair@ohms.com',  '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'DOCTOR', '9900000006'),
(7,  'Aman Gupta',    'aman.gupta@mail.com',    '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'PATIENT', '9900000007'),
(8,  'Sneha Kapoor',  'sneha.kapoor@mail.com',  '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'PATIENT', '9900000008'),
(9,  'Vikram Chauhan','vikram.chauhan@mail.com','$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'PATIENT', '9900000009'),
(10, 'Pooja Sharma',  'pooja.sharma@mail.com',  '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'PATIENT', '9900000010'),
(11, 'Rahul Yadav',   'rahul.yadav@mail.com',   '$2b$12$mvUW5UaMUHN0bKUdwG2T1.0fwmOaKA/DaqaiKfoOPQi1EQf3U9g32', 'PATIENT', '9900000011');

-- Hospitals
INSERT INTO hospitals (hospital_id, name, address, city, latitude, longitude, phone, emergency_available) VALUES
(1, 'SMS Hospital',            'JLN Marg, Jaipur',          'Jaipur', 26.9093, 75.8187, '01410000001', TRUE),
(2, 'Fortis Escorts Hospital', 'Jawahar Lal Nehru Marg',    'Jaipur', 26.8858, 75.8064, '01410000002', TRUE),
(3, 'Narayana Multispeciality','Sector 28, Pratap Nagar',   'Jaipur', 26.8515, 75.8092, '01410000003', TRUE),
(4, 'Manipal Hospital Jaipur', 'Sector 5, Vidhyadhar Nagar','Jaipur', 26.9465, 75.7645, '01410000004', TRUE),
(5, 'Mahatma Gandhi Hospital', 'Tonk Road, Jaipur',         'Jaipur', 26.8206, 75.8086, '01410000005', TRUE);

-- Doctors
INSERT INTO doctors (doctor_id, hospital_id, specialization, qualification, consult_fee) VALUES
(2, 1, 'Cardiology',      'MBBS, MD (Cardiology)', 800.00),
(3, 1, 'General Medicine','MBBS, MD',               500.00),
(4, 2, 'Orthopedics',     'MBBS, MS (Ortho)',       700.00),
(5, 3, 'Pediatrics',      'MBBS, MD (Pediatrics)',  600.00),
(6, 4, 'Dermatology',     'MBBS, MD (Dermatology)', 650.00);

-- Patients
INSERT INTO patients (patient_id, date_of_birth, gender, blood_group, address) VALUES
(7,  '1998-03-14', 'M', 'B+',  'Malviya Nagar, Jaipur'),
(8,  '2001-07-22', 'F', 'O+',  'C-Scheme, Jaipur'),
(9,  '1995-11-02', 'M', 'A+',  'Vaishali Nagar, Jaipur'),
(10, '1999-05-30', 'F', 'AB+', 'Mansarovar, Jaipur'),
(11, '1992-09-18', 'M', 'O-',  'Jagatpura, Jaipur');

-- Doctor Availability
INSERT INTO doctor_availability (doctor_id, slot_date, start_time, end_time, is_booked) VALUES
(2, CURRENT_DATE, '10:00:00', '10:30:00', TRUE),
(2, CURRENT_DATE, '11:00:00', '11:30:00', FALSE),
(2, CURRENT_DATE + 1, '09:00:00', '09:30:00', FALSE),
(3, CURRENT_DATE, '14:00:00', '14:30:00', FALSE),
(3, CURRENT_DATE, '15:00:00', '15:30:00', TRUE),
(3, CURRENT_DATE + 1, '10:00:00', '10:30:00', FALSE),
(4, CURRENT_DATE, '16:00:00', '16:30:00', FALSE),
(4, CURRENT_DATE + 1, '11:00:00', '11:30:00', FALSE),
(5, CURRENT_DATE, '09:30:00', '10:00:00', TRUE),
(5, CURRENT_DATE + 1, '09:30:00', '10:00:00', FALSE),
(6, CURRENT_DATE, '12:00:00', '12:30:00', FALSE),
(6, CURRENT_DATE, '13:00:00', '13:30:00', FALSE);

-- Appointments
INSERT INTO appointments (patient_id, doctor_id, slot_id, status, reason) VALUES
(7,  2, 1, 'CONFIRMED', 'Chest pain, follow-up'),
(8,  3, 5, 'COMPLETED', 'Fever and body ache'),
(9,  5, 9, 'PENDING',   'Child vaccination consult');

-- Medical Records
INSERT INTO medical_records (patient_id, doctor_id, appointment_id, diagnosis, prescription, notes) VALUES
(8, 3, 2, 'Viral fever', 'Paracetamol 500mg twice daily for 3 days, plenty of fluids', 'Advised rest; follow up if fever persists beyond 3 days');

-- Feedback
INSERT INTO feedback (appointment_id, rating, comments) VALUES
(2, 5, 'Dr. Rao was very thorough and explained everything clearly.');
