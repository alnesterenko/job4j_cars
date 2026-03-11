CREATE TABLE IF NOT EXISTS auto_user
(
    id SERIAL PRIMARY KEY,
    login VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    CONSTRAINT UNIQ_LOGIN_PASSWORD UNIQUE (login, password) /* Важный момент, про который лучше не забывать */
);
