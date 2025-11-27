-- Sample Users (Customers and Service Providers)
-- This test data file is for H2 test database only
INSERT INTO users (name, email, password, phone, role, active, created_at, updated_at) VALUES
('John Doe', 'john@example.com', 'password123', '1234567890', 'CUSTOMER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane Smith', 'jane@example.com', 'password123', '0987654321', 'CUSTOMER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dr. Sarah Wilson', 'dr.sarah@example.com', 'password123', '5551234567', 'SERVICE_PROVIDER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mike Johnson (Barber)', 'mike.barber@example.com', 'password123', '5559876543', 'SERVICE_PROVIDER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Emily Brown (Dentist)', 'emily.dentist@example.com', 'password123', '5555678901', 'SERVICE_PROVIDER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Admin User', 'admin@example.com', 'admin123', '5550000000', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Appointments
INSERT INTO appointments (customer_id, provider_id, service_type, appointment_datetime, notes, status, created_at, updated_at) VALUES
(1, 3, 'DOCTOR', '2025-11-25 10:00:00', 'Regular checkup', 'CONFIRMED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 4, 'BARBER', '2025-11-26 14:30:00', 'Haircut and styling', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 5, 'DENTIST', '2025-11-27 09:00:00', 'Teeth cleaning', 'CONFIRMED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, 'DOCTOR', '2025-11-28 11:00:00', 'Follow-up consultation', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
