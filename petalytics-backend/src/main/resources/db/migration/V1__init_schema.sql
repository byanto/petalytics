CREATE TABLE orders (
    id UUID PRIMARY KEY,
    version INTEGER,
    order_no VARCHAR(255) NOT NULL UNIQUE,
    marketplace VARCHAR(50) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    username VARCHAR(255),
    shipping_province VARCHAR(255) NOT NULL,
    shipping_city VARCHAR(255) NOT NULL,
    total_quantity INTEGER NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    completed_date TIMESTAMP
);

CREATE INDEX idx_order_province ON orders(shipping_province);
CREATE INDEX idx_order_city ON orders(shipping_city);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    version INTEGER,
    order_id UUID NOT NULL,
    sku VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_order_items_order_id FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);