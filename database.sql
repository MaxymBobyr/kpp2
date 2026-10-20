-- =====================================================================
-- СКРИПТ СТВОРЕННЯ БАЗИ ДАНИХ ДЛЯ КУРСОВОГО ПРОЄКТУ
-- Тема: Облік та обслуговування обладнання
-- =====================================================================

-- 1. СТВОРЕННЯ БАЗИ ДАНИХ
-- (Розкоментуйте ці рядки, якщо запускаєте скрипт через консоль PostgreSQL/MySQL)
DROP DATABASE IF EXISTS equipment_db;
CREATE DATABASE equipment_db;
-- \c equipment_db; -- Для PostgreSQL (або USE equipment_db; для MySQL)

-- 2. ВИДАЛЕННЯ ТАБЛИЦЬ (для можливості повторного запуску скрипта)
-- Порядок важливий: спочатку видаляємо таблиці з зовнішніми ключами
DROP TABLE IF EXISTS service_record;
DROP TABLE IF EXISTS service_request;
DROP TABLE IF EXISTS specialist;
DROP TABLE IF EXISTS equipment;

-- 3. СТВОРЕННЯ ТАБЛИЦЬ

-- Таблиця 1: Обладнання
CREATE TABLE equipment (
                           id BIGINT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           model VARCHAR(255),
                           serial_number VARCHAR(100) UNIQUE NOT NULL,
                           status VARCHAR(50) NOT NULL,
                           location VARCHAR(255)
);

-- Таблиця 2: Фахівці
CREATE TABLE specialist (
                            id BIGINT PRIMARY KEY,
                            full_name VARCHAR(255) NOT NULL,
                            specialization VARCHAR(100),
                            phone VARCHAR(20),
                            availability_status VARCHAR(50)
);

-- Таблиця 3: Заявки на обслуговування
CREATE TABLE service_request (
                                 id BIGINT PRIMARY KEY,
                                 equipment_id BIGINT NOT NULL,
                                 description TEXT,
                                 priority VARCHAR(50),
                                 status VARCHAR(50),
                                 created_date DATE,
    -- Зовнішній ключ (FK) на таблицю обладнання
                                 CONSTRAINT fk_request_equipment FOREIGN KEY (equipment_id)
                                     REFERENCES equipment (id) ON DELETE CASCADE
);

-- Таблиця 4: Сервісні записи (факти виконання робіт)
CREATE TABLE service_record (
                                id BIGINT PRIMARY KEY,
                                request_id BIGINT NOT NULL,
                                specialist_id BIGINT NOT NULL,
                                work_description TEXT,
                                completion_date DATE,
                                time_spent INTEGER,
    -- Зовнішні ключі (FK)
                                CONSTRAINT fk_record_request FOREIGN KEY (request_id)
                                    REFERENCES service_request (id) ON DELETE CASCADE,
                                CONSTRAINT fk_record_specialist FOREIGN KEY (specialist_id)
                                    REFERENCES specialist (id) ON DELETE CASCADE
);

-- 4. ДОДАВАННЯ ПОЧАТКОВИХ (ДЕМОНСТРАЦІЙНИХ) ДАНИХ

-- Додаємо обладнання
INSERT INTO equipment (id, name, model, serial_number, status, location) VALUES
                                                                             (1, 'Верстат ЧПУ', 'Fanuc-2000', 'SN-12345', 'ACTIVE', 'Цех №1'),
                                                                             (2, 'Компресор', 'Atlas Copco', 'SN-98765', 'BROKEN', 'Підвал'),
                                                                             (3, 'Генератор', 'Kohler', 'SN-55555', 'MAINTENANCE', 'Дах');

-- Додаємо фахівців
INSERT INTO specialist (id, full_name, specialization, phone, availability_status) VALUES
                                                                                       (1, 'Іваненко Іван', 'Механік', '+380501234567', 'AVAILABLE'),
                                                                                       (2, 'Петренко Петро', 'Електрик', '+380671234567', 'BUSY');

-- Додаємо заявки на обслуговування
INSERT INTO service_request (id, equipment_id, description, priority, status, created_date) VALUES
                                                                                                (1, 2, 'Не вмикається, чути сторонній шум', 'HIGH', 'NEW', '2023-10-25'),
                                                                                                (2, 1, 'Планове технічне обслуговування', 'LOW', 'DIAGNOSED', '2023-10-26'),
                                                                                                (3, 3, 'Перегрів під час роботи', 'CRITICAL', 'SERVICING', '2023-10-27');

-- Додаємо сервісні записи (звіти про виконану роботу)
INSERT INTO service_record (id, request_id, specialist_id, work_description, completion_date, time_spent) VALUES
                                                                                                              (1, 3, 1, 'Замінено систему охолодження', '2023-10-28', 120),
                                                                                                              (2, 1, 2, 'Діагностика двигуна', '2023-10-27', 45);