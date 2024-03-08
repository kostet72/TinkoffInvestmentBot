CREATE TABLE users (
    tag TEXT UNIQUE,
    chat_id BIGINT,
    is_subscribed BOOLEAN DEFAULT false::BOOLEAN
);

CREATE TABLE stock (
    ticker TEXT UNIQUE,
    price TEXT
);