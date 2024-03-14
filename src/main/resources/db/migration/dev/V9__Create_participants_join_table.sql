CREATE TABLE IF NOT EXISTS participants
(
    user_id  BIGINT,
    event_id BIGINT,
    PRIMARY KEY (user_id, event_id),
    CONSTRAINT fk_participant_user
        FOREIGN KEY (user_id)
            REFERENCES user (id),
    CONSTRAINT fk_participant_event
        FOREIGN KEY (event_id)
            REFERENCES event (id)
);
