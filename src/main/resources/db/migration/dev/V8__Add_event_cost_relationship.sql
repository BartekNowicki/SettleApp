ALTER TABLE cost
    ADD COLUMN event_id BIGINT;

ALTER TABLE cost
    ADD CONSTRAINT fk_cost_event
        FOREIGN KEY (event_id)
            REFERENCES event (id);