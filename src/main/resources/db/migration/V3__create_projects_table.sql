CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    url VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    user_id SERIAL NOT NULL,
    check_interval_in_minutes INTEGER,
    timeout_in_seconds INTEGER,
    success_threshold INTEGER,
    failure_threshold INTEGER,
    last_status VARCHAR(50),
    last_checked_at TIMESTAMP,
    uptime_percentage DECIMAL(5,2),
    total_uptime_in_seconds BIGINT,
    total_downtime_in_seconds BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);