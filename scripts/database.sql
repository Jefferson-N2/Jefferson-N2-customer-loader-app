-- Create database
Drop DATABASE IF EXISTS payroll_db;
CREATE DATABASE IF NOT EXISTS payroll_db;
USE payroll_db;

DROP DATABASE IF EXISTS payroll_db;
CREATE DATABASE IF NOT EXISTS payroll_db;
USE payroll_db;

CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_code VARCHAR(50) NOT NULL UNIQUE,
    id_type CHAR(1) NOT NULL,            
    id_number VARCHAR(50) NOT NULL UNIQUE,
    first_names VARCHAR(100),
    last_names VARCHAR(100),
    birth_date DATE,
    join_date DATE,
    email VARCHAR(100),
    phone_number VARCHAR(20)
);

CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    payroll_value DECIMAL(12,2),
    status VARCHAR(20),
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE payroll_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status CHAR(1),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE bulk_load_errors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_code VARCHAR(50),
    id_type CHAR(1),
    id_number VARCHAR(50),
    row_number INT NOT NULL,
    error_message VARCHAR(500) NOT NULL,
    error_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    processing_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_client_code (client_code),
    INDEX idx_id_number (id_number),
    INDEX idx_file_name (file_name),
    INDEX idx_processing_date (processing_date)
);
