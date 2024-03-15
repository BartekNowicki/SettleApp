CREATE TABLE IF NOT EXISTS user
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    fname             VARCHAR(255),
    lname             VARCHAR(255),
    email             VARCHAR(255) UNIQUE                     NOT NULL,
    password          VARCHAR(255)                            NOT NULL,
    creation_date     TIMESTAMP DEFAULT '2024-01-01 00:00:00' NOT NULL,
    modification_date TIMESTAMP DEFAULT '2024-01-01 00:00:00' NOT NULL
);
