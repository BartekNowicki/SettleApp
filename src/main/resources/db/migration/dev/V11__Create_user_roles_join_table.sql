CREATE TABLE IF NOT EXISTS user_role
(
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_table (user_id),
    FOREIGN KEY (role_id) REFERENCES role (role_id)
);
