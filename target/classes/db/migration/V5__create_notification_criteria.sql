CREATE TABLE notification_criteria (
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    city        VARCHAR(100),
    company     VARCHAR(100),
    keyword     VARCHAR(100),
    salary      VARCHAR(50),
    last_checked_at TIMESTAMP NOT NULL DEFAULT NOW()
);