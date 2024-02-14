CREATE TABLE IF NOT EXISTS cost
(
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255)   NOT NULL,
    quantity   INTEGER        NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL
);
