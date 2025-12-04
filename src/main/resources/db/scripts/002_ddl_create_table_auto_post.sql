CREATE TABLE IF NOT EXISTS auto_post
(
    id SERIAL PRIMARY KEY,
    description VARCHAR NOT NULL,
    created TIMESTAMP NOT NULL,
    auto_user_id INT REFERENCES auto_user (id) NOT NULL
);
