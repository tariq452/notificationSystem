-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS pg_cron;

-- Create sequence for new `id` primary key in notification_email
CREATE SEQUENCE notification_email_seq START 1 INCREMENT 1;

-- Create sequence for new `id` primary key in notification_email_archive
CREATE SEQUENCE notification_email_archive_seq START 1 INCREMENT 1;

-- Create table for tracking running pods (ULID as PK)
CREATE TABLE pod_tracking (
    ulid TEXT PRIMARY KEY,  -- ULID as the primary identifier
    pod_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    terminated_at TIMESTAMP NULL,
    status VARCHAR(20) CHECK (status IN ('RUNNING', 'TERMINATED')) DEFAULT 'RUNNING'
);

-- Create table for email notifications
CREATE TABLE notification_email (
    id BIGINT PRIMARY KEY DEFAULT nextval('notification_email_seq'),  -- New PK (Auto-incremented)
    ulid TEXT UNIQUE NOT NULL,  -- ULID for unique identification
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    attachment BYTEA,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED')) DEFAULT 'PENDING',
    retry_count INT DEFAULT 0,
    pod_ulid TEXT,  -- Foreign key to pod_tracking(ulid)
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (pod_ulid) REFERENCES pod_tracking(ulid) ON DELETE SET NULL
);

-- Create archive table for old emails
CREATE TABLE notification_email_archive (
    id BIGINT PRIMARY KEY DEFAULT nextval('notification_email_archive_seq'),  -- New PK
    ulid TEXT UNIQUE NOT NULL,  -- ULID for unique identification
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    attachment BYTEA,
    status VARCHAR(20),
    retry_count INT DEFAULT 0,
    pod_ulid TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_notification_status ON notification_email (status);
CREATE INDEX idx_notification_pod ON notification_email (pod_ulid);
CREATE INDEX idx_notification_created_at ON notification_email (created_at);

CREATE INDEX idx_archive_email ON notification_email_archive (email);
CREATE INDEX idx_archive_status ON notification_email_archive (status);
CREATE INDEX idx_archive_created_at ON notification_email_archive (created_at);

-- Function to move old emails to archive table
CREATE OR REPLACE FUNCTION move_old_emails_to_archive() RETURNS VOID AS $$
BEGIN
    INSERT INTO notification_email_archive (id, ulid, email, subject, message, attachment, status, retry_count, pod_ulid, created_at, updated_at)
    SELECT nextval('notification_email_archive_seq'), ulid, email, subject, message, attachment, status, retry_count, pod_ulid, created_at, updated_at
    FROM notification_email
    WHERE created_at < NOW() - INTERVAL '90 days';

    DELETE FROM notification_email WHERE created_at < NOW() - INTERVAL '90 days';
END;
$$ LANGUAGE plpgsql;

-- Function to cleanup old pod tracking records
CREATE OR REPLACE FUNCTION cleanup_terminated_pods() RETURNS VOID AS $$
BEGIN
    DELETE FROM pod_tracking WHERE terminated_at < NOW() - INTERVAL '30 days';
END;
$$ LANGUAGE plpgsql;

-- Schedule automatic cleanup jobs
SELECT cron.schedule('0 2 * * *', 'CALL move_old_emails_to_archive()');
SELECT cron.schedule('0 3 * * *', 'CALL cleanup_terminated_pods()');
