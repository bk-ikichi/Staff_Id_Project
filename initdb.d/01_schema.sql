-- 1. 店舗マスタ
CREATE TABLE stores (
                        id SERIAL PRIMARY KEY,
                        com_id VARCHAR(20) UNIQUE NOT NULL,
                        com_name VARCHAR(100) NOT NULL,
                        location VARCHAR(255),
                        is_active BOOLEAN DEFAULT TRUE
);

-- 2. スタッフマスタ
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       staff_id VARCHAR(20) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL, -- BCryptハッシュ用
                       name VARCHAR(50) NOT NULL,
                       role VARCHAR(20) NOT NULL, -- 高校生, 大学生, 専門生, パート, 社員, 管理者
                       store_id INT REFERENCES stores(id),
                       status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE
--                        device_id VARCHAR(255) UNIQUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 利用ログ
CREATE TABLE usage_logs (
                            id SERIAL PRIMARY KEY,
                            user_id INT REFERENCES users(id),
                            used_at_store_id INT REFERENCES stores(id),
                            used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- サンプルの店舗データを1つ入れておく（開発用）
INSERT INTO stores (id,com_id, com_name, location, is_active) VALUES (1,'h001', '株式会社伊吉', '青森県八戸市',true);