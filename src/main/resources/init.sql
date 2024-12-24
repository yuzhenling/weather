-- 创建数据库
CREATE DATABASE IF NOT EXISTS weather_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE weather_db;

-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 城市表
CREATE TABLE cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    province VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 天气数据表
CREATE TABLE weather_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    city_id BIGINT NOT NULL,
    date DATE NOT NULL,
    temperature DOUBLE,
    humidity DOUBLE,
    weather VARCHAR(50),
    wind_direction VARCHAR(50),
    wind_speed DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (city_id) REFERENCES cities(id)
);

-- 创建索引
CREATE INDEX idx_city_date ON weather_data(city_id, date);

-- 插入初始管理员用户（密码：admin123）
INSERT INTO users (username, password, role) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt2LzyO', 'ADMIN');

-- 插入一些测试城市数据
INSERT INTO cities (name, province, description) VALUES 
('北京', '北京市', '中国首都'),
('上海', '上海市', '中国经济中心'),
('广州', '广东省', '广东省省会');

-- 插入一些测试天气数据
INSERT INTO weather_data (city_id, date, temperature, humidity, weather, wind_direction, wind_speed) VALUES 
(1, CURRENT_DATE, 20.5, 65, '晴', '东北风', 3.2),
(2, CURRENT_DATE, 22.0, 70, '多云', '东风', 2.8),
(3, CURRENT_DATE, 25.5, 75, '阴', '南风', 2.5);