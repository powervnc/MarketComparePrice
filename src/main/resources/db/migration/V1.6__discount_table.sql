CREATE TABLE DISCOUNT (
   discount_id VARCHAR(255) PRIMARY KEY,
   from_date DATE NOT NULL,
   to_date DATE NOT NULL,
   percentage_discount INT NOT NULL CHECK (percentage_discount BETWEEN 1 AND 99),
   product_id VARCHAR(255) NOT NULL,
   store_id VARCHAR(255) NOT NULL,
   CONSTRAINT fk_store FOREIGN KEY (store_id) REFERENCES STORE(store_id),
   CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id),
   UNIQUE (store_id, product_id, from_date, to_date),
   CONSTRAINT chk_date CHECK (from_date <= to_date)
)


