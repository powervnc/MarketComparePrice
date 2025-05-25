CREATE TABLE PRODUCT (
    product_id VARCHAR(255) PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    package_quantity REAL NOT NULL,
    package_unit VARCHAR(255) NOT NULL,
    category_id VARCHAR(255) NOT NULL,
    brand_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES CATEGORY(category_id),
    CONSTRAINT fk_brand FOREIGN KEY (brand_id) REFERENCES BRAND(brand_id)
);
