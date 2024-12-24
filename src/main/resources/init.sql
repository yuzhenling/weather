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






DELIMITER //
CREATE PROCEDURE weather_db.insert_test_weather_data()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE city_id INT;
    DECLARE cur_date DATE;
    
    -- 遍历城市
    DECLARE city_cursor CURSOR FOR SELECT id FROM cities;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET i = -1;
    
    OPEN city_cursor;
    
    city_loop: LOOP
        FETCH city_cursor INTO city_id;
        IF i = -1 THEN
            LEAVE city_loop;
        END IF;
        
        -- 插入近7天的数据
        SET i = 0;
        WHILE i < 7 DO
            SET cur_date = DATE_SUB(CURDATE(), INTERVAL i DAY);
            
            -- 生成随机天气数据
            INSERT INTO weather_data (
                city_id, 
                date, 
                temperature, 
                humidity, 
                weather, 
                wind_direction, 
                wind_speed
            ) VALUES (
                city_id,
                cur_date,
                15 + RAND() * 15, -- 温度范围：15-30度
                50 + RAND() * 40, -- 湿度范围：50-90%
                ELT(FLOOR(1 + RAND() * 4), '晴', '多云', '阴', '小雨'),
                ELT(FLOOR(1 + RAND() * 4), '东风', '南风', '西风', '北风'),
                2 + RAND() * 5    -- 风速范围：2-7米/秒
            ) ON DUPLICATE KEY UPDATE
                temperature = VALUES(temperature),
                humidity = VALUES(humidity),
                weather = VALUES(weather),
                wind_direction = VALUES(wind_direction),
                wind_speed = VALUES(wind_speed);
            
            SET i = i + 1;
        END WHILE;
    END LOOP;
    
    CLOSE city_cursor;
END //
DELIMITER ;


CALL weather_db.insert_test_weather_data();


DROP PROCEDURE IF EXISTS insert_test_weather_data;