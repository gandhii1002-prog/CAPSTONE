-- Existing V1-V3 constraints enforce data integrity for users, products,
-- orders, and reviews. These composite indexes support the buyer browse path.

CREATE INDEX IF NOT EXISTS idx_products_active_category_created
    ON products(active, category, created_at);

CREATE INDEX IF NOT EXISTS idx_products_active_price
    ON products(active, price);

CREATE INDEX IF NOT EXISTS idx_products_active_stock
    ON products(active, stock_quantity);

CREATE INDEX IF NOT EXISTS idx_orders_buyer_status
    ON orders(buyer_id, status);