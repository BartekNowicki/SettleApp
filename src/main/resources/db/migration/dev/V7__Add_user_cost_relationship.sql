ALTER TABLE cost
    ADD COLUMN user_id BIGINT;

ALTER TABLE cost
    ADD CONSTRAINT fk_cost_user
        FOREIGN KEY (user_id)
            REFERENCES user(id);
