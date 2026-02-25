CREATE TABLE IF NOT EXISTS user (
  id bigint(20) AUTO_INCREMENT PRIMARY KEY,
  password varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  last_blocked_at DATETIME DEFAULT NULL
)