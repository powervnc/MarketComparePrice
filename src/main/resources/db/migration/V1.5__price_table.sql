CREATE TABLE PRICE (
    price_id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id),
    store_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_store FOREIGN KEY (store_id) REFERENCES STORE(store_id),
    price REAL NOT NULL CHECK (price > 0),
    currency VARCHAR(3) NOT NULL,
    price_date DATE NOT NULL,
    UNIQUE (product_id, store_id, price_date)
)

