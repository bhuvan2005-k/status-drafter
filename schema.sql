-- Status Drafter Tool Database Schema
-- Database: status_drafter_db

CREATE DATABASE IF NOT EXISTS status_drafter_db;
USE status_drafter_db;

-- 1. Users Table: Individual employees or team members
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    team_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Parsing Rules: DB-driven keywords for the classification engine
CREATE TABLE IF NOT EXISTS parsing_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category ENUM('YESTERDAY', 'TODAY', 'BLOCKERS') NOT NULL,
    keyword VARCHAR(50) NOT NULL UNIQUE,
    priority INT DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- 3. Status Records: Main storage for status updates
CREATE TABLE IF NOT EXISTS status_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    raw_content TEXT NOT NULL,
    parsed_yesterday TEXT,
    parsed_today TEXT,
    parsed_blockers TEXT,
    total_hours_spent DECIMAL(4,2) DEFAULT 0.0,
    confidence_score DECIMAL(3,2) DEFAULT 0.0,
    status_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_date (user_id, status_date),
    INDEX idx_status_date (status_date)
) ENGINE=InnoDB;

-- Sample Data for Interview Demo
INSERT INTO users (username, email, full_name, team_name) VALUES 
('jdoe', 'john.doe@example.com', 'John Doe', 'Backend Team');

INSERT INTO parsing_rules (category, keyword, priority) VALUES 
('YESTERDAY', 'worked on', 1),
('YESTERDAY', 'completed', 2),
('YESTERDAY', 'finished', 2),
('TODAY', 'plan to', 1),
('TODAY', 'will do', 1),
('TODAY', 'going to', 1),
('BLOCKERS', 'stuck', 3),
('BLOCKERS', 'blocked', 3),
('BLOCKERS', 'issue', 2);
