CREATE TABLE IF NOT EXISTS event
(
    event_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    status             VARCHAR(255) NOT NULL,
    event_date         DATE         NOT NULL,
    created_by_user_id BIGINT       NOT NULL
);
