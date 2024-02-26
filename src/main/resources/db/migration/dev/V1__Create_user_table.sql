CREATE TABLE IF NOT EXISTS user_table
(
    user_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    fname    VARCHAR(255)        NOT NULL,
    lname    VARCHAR(255)        NOT NULL,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL
);
