CREATE TABLE users (
    tag TEXT UNIQUE,
    chat_id TEXT
);

CREATE TABLE stock (
    ticker TEXT UNIQUE,
    price TEXT
);