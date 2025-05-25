CREATE TABLE STORE (
    store_id VARCHAR(255) PRIMARY KEY,
    store_name VARCHAR(255) NOT NULL,
    UNIQUE (store_name)
);
