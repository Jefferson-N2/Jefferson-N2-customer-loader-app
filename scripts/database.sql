-- Create database
DROP DATABASE IF EXISTS payroll_db;
CREATE DATABASE IF NOT EXISTS payroll_db;
USE payroll_db;

-- ============================================
-- Tabla de procesos de carga masiva
-- ============================================
CREATE TABLE bulk_load_processes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_id VARCHAR(36) NOT NULL UNIQUE,
    file_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_records INT,
    successful_count INT DEFAULT 0,
    error_count INT DEFAULT 0,
    processing_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now() ON UPDATE now(),
    INDEX idx_process_id (process_id),
    INDEX idx_status (status)
);

-- ============================================
-- Tabla de clientes (1:1 con cuentas)
-- ============================================
CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_code VARCHAR(50) NOT NULL UNIQUE,
    id_type CHAR(1) NOT NULL,            
    id_number VARCHAR(50) NOT NULL UNIQUE,
    first_names VARCHAR(100) NOT NULL,
    last_names VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    join_date DATE NOT NULL,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    process_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT now(),
    FOREIGN KEY (process_id) REFERENCES bulk_load_processes(process_id) ON DELETE CASCADE,
    INDEX idx_process_id (process_id),
    INDEX idx_client_code (client_code),
    INDEX idx_id_number (id_number)
);

-- ============================================
-- Tabla de cuentas (1:N con pagos)
-- ============================================
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL UNIQUE,
    payroll_value DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now() ON UPDATE now(),
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    INDEX idx_account_number (account_number),
    INDEX idx_client_id (client_id)
);

-- ============================================
-- Tabla de pagos (historial de transacciones)
-- ============================================
CREATE TABLE payroll_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT now(),
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    INDEX idx_account_id (account_id),
    INDEX idx_payment_date (payment_date),
    INDEX idx_status (status)
);

-- ============================================
-- Tabla de errores de carga masiva
-- ============================================
CREATE TABLE bulk_load_errors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_id VARCHAR(36) NOT NULL,
    client_code VARCHAR(50),
    id_type CHAR(1),
    id_number VARCHAR(50),
    row_index INT NOT NULL,
    error_message VARCHAR(500) NOT NULL,
    error_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    processing_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    FOREIGN KEY (process_id) REFERENCES bulk_load_processes(process_id) ON DELETE CASCADE,
    INDEX idx_process_id (process_id),
    INDEX idx_client_code (client_code)
);
