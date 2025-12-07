-- SQLite Database Schema for SecureVent Audit Logs
-- Database: data/securevent_audit.db
-- Purpose: Store non-sensitive metadata about operations

CREATE TABLE IF NOT EXISTS audit_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    operation TEXT NOT NULL CHECK(operation IN ('HIDE', 'REVEAL', 'DURESS_ACTIVATED')),
    image_filename TEXT NOT NULL,
    entropy_score REAL,
    status TEXT NOT NULL CHECK(status IN ('SUCCESS', 'FAILED', 'SKIPPED')),
    error_message TEXT,
    notes TEXT
);

-- Create index on timestamp for faster queries
CREATE INDEX IF NOT EXISTS idx_audit_timestamp 
ON audit_logs(timestamp DESC);

-- Create index on operation type
CREATE INDEX IF NOT EXISTS idx_audit_operation 
ON audit_logs(operation);

-- Create index on status
CREATE INDEX IF NOT EXISTS idx_audit_status 
ON audit_logs(status);

-- View: Daily Activity Summary
CREATE VIEW IF NOT EXISTS daily_activity AS
SELECT
    DATE(timestamp) as date,
    COUNT(*) as total_operations,
    SUM(CASE WHEN operation = 'HIDE' THEN 1 ELSE 0 END) as hide_count,
    SUM(CASE WHEN operation = 'REVEAL' THEN 1 ELSE 0 END) as reveal_count,
    SUM(CASE WHEN operation = 'DURESS_ACTIVATED' THEN 1 ELSE 0 END) as duress_count,
    AVG(entropy_score) as avg_entropy
FROM audit_logs
GROUP BY DATE(timestamp)
ORDER BY date DESC;

-- Sample queries for Java code:

-- 1. Log a successful HIDE operation:
-- INSERT INTO audit_logs 
-- (operation, image_filename, entropy_score, status) 
-- VALUES ('HIDE', 'forest_photo.png', 5.87, 'SUCCESS');

-- 2. Log a failed REVEAL operation:
-- INSERT INTO audit_logs 
-- (operation, image_filename, entropy_score, status, error_message) 
-- VALUES ('REVEAL', 'corrupted.png', 0.0, 'FAILED', 'Image format invalid');

-- 3. Retrieve all operations from today:
-- SELECT * FROM audit_logs 
-- WHERE DATE(timestamp) = DATE('now')
-- ORDER BY timestamp DESC;
