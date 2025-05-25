CREATE TABLE USER_TARGET_PRICE (
    user_target_id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id),
    user_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES APP_USER(user_id),
    UNIQUE (product_id, user_id),
    threshold REAL NOT NULL CHECK (threshold > 0)
)