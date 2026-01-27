CREATE TABLE IF NOT EXISTS jwt_tokens (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(512) NOT NULL COLLATE ascii_bin,
    user_id bigint(20) NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'BLOCKED') NOT NULL,
    issued_at TIMESTAMP(6) NOT NULL,
    expires_at TIMESTAMP(6) NULL,
    last_used_at TIMESTAMP(6) NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_expires_at (expires_at),
    CONSTRAINT fk_jwt_user FOREIGN KEY (user_id) REFERENCES user(id)
)