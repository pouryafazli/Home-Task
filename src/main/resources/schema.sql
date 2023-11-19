CREATE TABLE IF NOT EXISTS Fund (
    id INT NOT NULL,
    customer_id INT NOT NULL,
    load_amount DECIMAL(10, 2)  NOT NULL,
    time TIMESTAMP NOT NULL,
    PRIMARY KEY (id, customer_id)
);

ALTER TABLE Fund ADD CONSTRAINT CK_amount CHECK (load_amount IS NOT NULL AND load_amount >= 0);
ALTER TABLE Fund ADD CONSTRAINT CK_createdAt CHECK (time IS NOT NULL);