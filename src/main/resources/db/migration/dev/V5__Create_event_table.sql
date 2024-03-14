CREATE TABLE IF NOT EXISTS event
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    status             VARCHAR(255)                            NOT NULL,
    event_date         DATE                                    NOT NULL,
    created_by_user_id BIGINT                                  NOT NULL,
    creation_date      TIMESTAMP DEFAULT '2024-01-01 00:00:00' NOT NULL,
    modification_date  TIMESTAMP DEFAULT '2024-01-01 00:00:00' NOT NULL
);
