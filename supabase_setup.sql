-- EssyCoff POS Database Schema
-- Supabase PostgreSQL Setup Script

-- Enable pgcrypto extension for password hashing
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Enable Row Level Security
ALTER DATABASE postgres SET "app.jwt_secret" TO 'your-jwt-secret-here';

-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('staff', 'manager')),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Products Table
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('Kopi', 'Makanan', 'Minuman', 'Lainnya')),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    stock INTEGER DEFAULT 0 CHECK (stock >= 0),
    is_available BOOLEAN DEFAULT true,
    image_url VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    transaction_number VARCHAR(50) UNIQUE NOT NULL,
    cashier_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    tax_amount DECIMAL(10,2) NOT NULL CHECK (tax_amount >= 0),
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    payment_method VARCHAR(20) NOT NULL CHECK (payment_method IN ('Tunai', 'Kartu Debit', 'Kartu Kredit', 'E-Wallet')),
    payment_amount DECIMAL(10,2) NOT NULL CHECK (payment_amount >= 0),
    change_amount DECIMAL(10,2) DEFAULT 0 CHECK (change_amount >= 0),
    status VARCHAR(20) DEFAULT 'completed' CHECK (status IN ('pending', 'completed', 'cancelled')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Transaction Items Table
CREATE TABLE IF NOT EXISTS transaction_items (
    id SERIAL PRIMARY KEY,
    transaction_id INTEGER REFERENCES transactions(id) ON DELETE CASCADE,
    product_id INTEGER REFERENCES products(id) ON DELETE SET NULL,
    product_name VARCHAR(100) NOT NULL, -- Store product name for historical data
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price > 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal > 0),
    notes TEXT
);

-- Insert Default Users
-- Password akan otomatis di-hash oleh trigger hash_user_password
INSERT INTO users (username, password, full_name, role) VALUES
('manager', 'password123', 'Manager EssyCoff', 'manager'),
('staff', 'password123', 'Staff Kasir', 'staff')
ON CONFLICT (username) DO NOTHING;

-- Insert Sample Products
INSERT INTO products (name, category, price, stock, is_available, description) VALUES
-- Kopi
('Espresso', 'Kopi', 15000, 100, true, 'Kopi espresso klasik dengan rasa yang kuat'),
('Americano', 'Kopi', 18000, 100, true, 'Espresso dengan air panas, rasa yang lebih ringan'),
('Cappuccino', 'Kopi', 22000, 100, true, 'Espresso dengan susu steamed dan foam'),
('Latte', 'Kopi', 25000, 100, true, 'Espresso dengan susu steamed, rasa yang creamy'),
('Mocha', 'Kopi', 28000, 100, true, 'Espresso dengan cokelat dan susu steamed'),
('Macchiato', 'Kopi', 26000, 100, true, 'Espresso dengan sedikit susu steamed'),

-- Minuman
('Teh Tarik', 'Minuman', 12000, 100, true, 'Teh dengan susu yang ditarik'),
('Es Teh Manis', 'Minuman', 8000, 100, true, 'Teh manis dingin yang menyegarkan'),
('Jus Jeruk', 'Minuman', 15000, 50, true, 'Jus jeruk segar tanpa gula tambahan'),
('Smoothie Mangga', 'Minuman', 20000, 30, true, 'Smoothie mangga dengan yogurt'),
('Chocolate Milkshake', 'Minuman', 22000, 40, true, 'Milkshake cokelat dengan es krim'),

-- Makanan
('Croissant', 'Makanan', 18000, 20, true, 'Croissant butter yang renyah'),
('Sandwich Club', 'Makanan', 35000, 15, true, 'Sandwich dengan ayam, sayuran, dan saus'),
('Pasta Carbonara', 'Makanan', 45000, 10, true, 'Pasta dengan saus carbonara creamy'),
('Nasi Goreng Spesial', 'Makanan', 28000, 20, true, 'Nasi goreng dengan telur dan ayam'),
('Cake Cokelat', 'Makanan', 25000, 8, true, 'Slice cake cokelat dengan frosting'),
('Donat Glazed', 'Makanan', 12000, 25, true, 'Donat dengan glazed manis'),
('Muffin Blueberry', 'Makanan', 15000, 15, true, 'Muffin dengan blueberry segar')

ON CONFLICT DO NOTHING;

-- Create Indexes for Performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_available ON products(is_available);
CREATE INDEX IF NOT EXISTS idx_transactions_cashier ON transactions(cashier_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);
CREATE INDEX IF NOT EXISTS idx_transaction_items_transaction ON transaction_items(transaction_id);
CREATE INDEX IF NOT EXISTS idx_transaction_items_product ON transaction_items(product_id);

-- Create Updated At Trigger Function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create Password Hash Trigger Function
-- Function ini akan otomatis meng-hash password saat insert atau update
CREATE OR REPLACE FUNCTION hash_password()
RETURNS TRIGGER AS $$
BEGIN
    -- Cek apakah password berubah dan belum di-hash (tidak dimulai dengan $2a$)
    IF NEW.password IS DISTINCT FROM OLD.password OR (TG_OP = 'INSERT') THEN
        -- Jika password tidak dimulai dengan $2a$ (belum di-hash), maka hash password
        IF NEW.password NOT LIKE '$2a$%' THEN
            -- Hash password menggunakan bcrypt dengan cost 10
            NEW.password = crypt(NEW.password, gen_salt('bf', 10));
        END IF;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create Triggers for Updated At
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create Password Hash Trigger
-- Trigger ini akan otomatis meng-hash password sebelum insert atau update
CREATE TRIGGER hash_user_password BEFORE INSERT OR UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION hash_password();

-- Row Level Security Policies
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE transaction_items ENABLE ROW LEVEL SECURITY;

-- Allow all operations for authenticated users (you can customize this based on your needs)
CREATE POLICY "Allow all for authenticated users" ON users FOR ALL USING (auth.role() = 'authenticated');
CREATE POLICY "Allow all for authenticated users" ON products FOR ALL USING (auth.role() = 'authenticated');
CREATE POLICY "Allow all for authenticated users" ON transactions FOR ALL USING (auth.role() = 'authenticated');
CREATE POLICY "Allow all for authenticated users" ON transaction_items FOR ALL USING (auth.role() = 'authenticated');

-- Create Views for Reports
CREATE OR REPLACE VIEW daily_sales AS
SELECT 
    DATE(created_at) as sale_date,
    COUNT(*) as transaction_count,
    SUM(total_amount) as total_sales,
    AVG(total_amount) as average_transaction
FROM transactions 
WHERE status = 'completed'
GROUP BY DATE(created_at)
ORDER BY sale_date DESC;

CREATE OR REPLACE VIEW product_sales AS
SELECT 
    p.name as product_name,
    p.category,
    SUM(ti.quantity) as total_quantity,
    SUM(ti.subtotal) as total_revenue
FROM transaction_items ti
JOIN products p ON ti.product_id = p.id
JOIN transactions t ON ti.transaction_id = t.id
WHERE t.status = 'completed'
GROUP BY p.id, p.name, p.category
ORDER BY total_revenue DESC;

-- Grant necessary permissions
GRANT USAGE ON SCHEMA public TO anon, authenticated;
GRANT ALL ON ALL TABLES IN SCHEMA public TO anon, authenticated;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO anon, authenticated;

-- Comments
COMMENT ON TABLE users IS 'Tabel pengguna sistem (staff dan manager)';
COMMENT ON TABLE products IS 'Tabel produk yang dijual di kedai kopi';
COMMENT ON TABLE transactions IS 'Tabel transaksi penjualan';
COMMENT ON TABLE transaction_items IS 'Tabel detail item dalam setiap transaksi';
COMMENT ON VIEW daily_sales IS 'View untuk laporan penjualan harian';
COMMENT ON VIEW product_sales IS 'View untuk laporan penjualan per produk';
