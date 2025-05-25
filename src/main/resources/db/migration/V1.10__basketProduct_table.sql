CREATE TABLE BASKET_PRODUCT (
    basket_product_id VARCHAR(255) PRIMARY KEY,
    basket_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_basket FOREIGN KEY (basket_id) REFERENCES BASKET(basket_id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id),
    UNIQUE(basket_id, product_id),
    quantity INT NOT NULL CHECK (quantity > 0)
);
