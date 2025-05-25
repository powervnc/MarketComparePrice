CREATE TABLE BRAND (
    brand_id VARCHAR(255) PRIMARY KEY,
    brand_name VARCHAR(255) NOT NULL,
    UNIQUE (brand_name)
);
