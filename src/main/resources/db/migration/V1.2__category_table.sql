CREATE TABLE CATEGORY (
    category_id VARCHAR(255) PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    UNIQUE (category_name)
);
