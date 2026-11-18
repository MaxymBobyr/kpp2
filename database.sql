SET NAMES utf8mb4;

DROP DATABASE IF EXISTS equipment_db;
CREATE DATABASE equipment_db;
USE equipment_db;

DROP TABLE IF EXISTS service_record;
DROP TABLE IF EXISTS service_request;
DROP TABLE IF EXISTS specialist;
DROP TABLE IF EXISTS equipment;

CREATE TABLE equipment (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(255) NOT NULL,
                           model VARCHAR(255),
                           serial_number VARCHAR(100) UNIQUE NOT NULL,
                           status VARCHAR(50) NOT NULL,
                           location VARCHAR(255)
) DEFAULT CHARACTER SET utf8mb4;

CREATE TABLE specialist (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            full_name VARCHAR(255) NOT NULL,
                            specialization VARCHAR(100),
                            phone VARCHAR(20),
                            availability_status VARCHAR(50)
) DEFAULT CHARACTER SET utf8mb4;

CREATE TABLE service_request (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 equipment_id BIGINT NOT NULL,
                                 description TEXT,
                                 f1 DOUBLE DEFAULT 0,
                                 f2 DOUBLE DEFAULT 0,
                                 f3 DOUBLE DEFAULT 0,
                                 integral_indicator DOUBLE DEFAULT 0,
                                 status VARCHAR(50),
                                 created_date DATE,
                                 CONSTRAINT fk_request_equipment FOREIGN KEY (equipment_id)
                                     REFERENCES equipment (id) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8mb4;

CREATE TABLE service_record (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                request_id BIGINT NOT NULL,
                                specialist_id BIGINT NOT NULL,
                                work_description TEXT,
                                completion_date DATE,
                                time_spent INTEGER,
                                CONSTRAINT fk_record_request FOREIGN KEY (request_id)
                                    REFERENCES service_request (id) ON DELETE CASCADE,
                                CONSTRAINT fk_record_specialist FOREIGN KEY (specialist_id)
                                    REFERENCES specialist (id) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8mb4;

INSERT INTO equipment (id, name, model, serial_number, status, location) VALUES
    (1, 'Верстат ЧПУ Fanuc-2000', 'Fanuc-2000', 'SN-EQ-00001', 'ACTIVE', 'Цех №1'),
    (2, 'Компресор Atlas Copco', 'Atlas Copco', 'SN-EQ-00002', 'BROKEN', 'Підвал'),
    (3, 'Генератор Kohler 20kVA', 'Kohler', 'SN-EQ-00003', 'MAINTENANCE', 'Дах'),
    (4, 'Токарний верстат 16К20', '16К20', 'SN-EQ-00004', 'ACTIVE', 'Цех №2'),
    (5, 'Фрезерний верстат 6А82', '6А82', 'SN-EQ-00005', 'ACTIVE', 'Цех №2'),
    (6, 'Зварювальний апарат Prestige 250', 'Prestige 250', 'SN-EQ-00006', 'BROKEN', 'Зварювальна дільниця'),
    (7, 'Кран-балка 2т', 'КБ-2', 'SN-EQ-00007', 'ACTIVE', 'Цех №1'),
    (8, 'Пресс гідравлічний П6330', 'П6330', 'SN-EQ-00008', 'MAINTENANCE', 'Цех №3'),
    (9, 'Компресор Atlas Copco GA-90', 'GA-90', 'SN-EQ-00009', 'ACTIVE', 'Підвал'),
    (10, 'Електрогенератор Hatz 30kVA', 'Hatz', 'SN-EQ-000010', 'ACTIVE', 'Дах'),
    (11, 'Верстат свердлильний 2Н135', '2Н135', 'SN-EQ-000011', 'ACTIVE', 'Цех №2'),
    (12, 'Шліфувальний верстат 3Л135', '3Л135', 'SN-EQ-000012', 'BROKEN', 'Цех №3'),
    (13, 'Пневмопрес P-40', 'P-40', 'SN-EQ-000013', 'ACTIVE', 'Цех №1'),
    (14, 'Теплогенератор Термоленд 50', 'Термоленд-50', 'SN-EQ-000014', 'MAINTENANCE', 'Дах'),
    (15, 'Верстат фрезерний Haas VF-2', 'Haas VF-2', 'SN-EQ-000015', 'ACTIVE', 'Цех №3'),
    (16, 'Станок свердлильний JET JDP-15', 'JDP-15', 'SN-EQ-000016', 'ACTIVE', 'Цех №2'),
    (17, 'Кран консольний 1т', 'КК-1', 'SN-EQ-000017', 'ACTIVE', 'Склад'),
    (18, 'Компресор Remeika RM 50', 'RM-50', 'SN-EQ-000018', 'BROKEN', 'Підвал'),
    (19, 'Електротеплова гарячача плита ПВЭ-0,7', 'ПВЭ-0,7', 'SN-EQ-000019', 'ACTIVE', 'Їдальня'),
    (20, 'Промисловий пилосос Karcher WD-5', 'WD-5', 'SN-EQ-000020', 'MAINTENANCE', 'Склад');

INSERT INTO specialist (id, full_name, specialization, phone, availability_status) VALUES
    (1, 'Іваненко Іван', 'Механік', '+380501234567', 'AVAILABLE'),
    (2, 'Петренко Петро', 'Електрик', '+380671234567', 'BUSY'),
    (3, 'Ковальчук Олег', 'Зварювальник', '+380509876543', 'AVAILABLE'),
    (4, 'Бондаренко Сергій', 'Токар', '+380501112233', 'AVAILABLE'),
    (5, 'Ткаченко Андрій', 'Фрезерувальник', '+380674445566', 'BUSY'),
    (6, 'Шевченко Микола', 'Електромонтажник', '+380509998877', 'AVAILABLE'),
    (7, 'Мельник Василь', 'Газовик', '+380669887766', 'AVAILABLE'),
    (8, 'Савченко Тарас', 'Кранівник', '+380501234598', 'BUSY'),
    (9, 'Романенко Олег', 'Моторист', '+380679876543', 'AVAILABLE'),
    (10, 'Левченко Дмитро', 'Електротехнік', '+380505554433', 'AVAILABLE'),
    (11, 'Пилипчук Юрій', 'Верстатник', '+380672223344', 'BUSY'),
    (12, 'Марченко Ігор', 'Компресорник', '+380501119988', 'AVAILABLE'),
    (13, 'Гончаренко Роман', 'Електрик', '+380667778899', 'AVAILABLE'),
    (14, 'Кравченко Віктор', 'Механік', '+380504445566', 'AVAILABLE'),
    (15, 'Олійник Назар', 'Зварювальник', '+380673332211', 'BUSY'),
    (16, 'Сидоренко Богдан', 'Наладчик', '+380507776655', 'AVAILABLE'),
    (17, 'Юрченко Тарас', 'Токар', '+380669911223', 'AVAILABLE'),
    (18, 'Дорошенко Ігор', 'Гідравлік', '+380503322114', 'AVAILABLE'),
    (19, 'Панченко Валерій', 'Електрик', '+380678889900', 'BUSY'),
    (20, 'Ковальчук Максим', 'Універсал', '+380505443322', 'AVAILABLE');

INSERT INTO service_request (id, equipment_id, description, f1, f2, f3, integral_indicator, status, created_date) VALUES
    (1, 2, 'Не вмикається, чути сторонній шум', 50, 60, 70, 59.3, 'NEW', '2026-01-05'),
    (2, 1, 'Планове технічне обслуговування верстата', 30, 40, 50, 38.5, 'DIAGNOSED', '2026-01-08'),
    (3, 3, 'Перегрів під час роботи', 80, 70, 90, 78.5, 'IN_PROGRESS', '2026-01-12'),
    (4, 5, 'Заміна інструментальних різаць', 20, 30, 40, 28.5, 'COMPLETED', '2026-01-15'),
    (5, 6, 'Не працює регулятор зварювального струму', 60, 70, 80, 68.5, 'NEW', '2026-02-01'),
    (6, 7, 'Постійне скриплення при підйомі', 10, 20, 30, 18.5, 'DIAGNOSED', '2026-02-04'),
    (7, 8, 'Тече гідравлічна олива в пресі', 90, 80, 70, 81.5, 'IN_PROGRESS', '2026-02-09'),
    (8, 9, 'Знижений тиск у компресорі', 40, 50, 60, 48.5, 'COMPLETED', '2026-02-14'),
    (9, 10, 'Не запускається генератор з автоматичного пуску', 70, 80, 90, 78.5, 'NEW', '2026-03-02'),
    (10, 11, 'Свердло не центрується на деталі', 25, 35, 45, 34.0, 'DIAGNOSED', '2026-03-07'),
    (11, 12, 'Шліфувальний круг зношений до мінімуму', 55, 65, 75, 64.0, 'IN_PROGRESS', '2026-03-11'),
    (12, 14, 'Теплогенератор не виходить на потужність', 35, 45, 55, 44.0, 'COMPLETED', '2026-03-18'),
    (13, 16, 'Свердлильний патрон заклинило', 85, 75, 65, 76.0, 'NEW', '2026-04-01'),
    (14, 17, 'Підкраномі гальмівні колодки стерлися', 15, 25, 35, 24.0, 'DIAGNOSED', '2026-04-05'),
    (15, 18, 'Компресор аварійно зупинився', 95, 85, 75, 86.0, 'IN_PROGRESS', '2026-04-10'),
    (16, 19, 'Плита не нагрівається', 5, 15, 25, 14.0, 'COMPLETED', '2026-04-16'),
    (17, 20, 'Пилосос втратив потужність всмоктування', 45, 55, 65, 54.0, 'NEW', '2026-05-02'),
    (18, 4, 'Бирка зварювального верстата', 20, 30, 40, 28.5, 'DIAGNOSED', '2026-05-08'),
    (19, 3, 'Не спрацьовує автоматика запуску', 65, 75, 85, 74.0, 'IN_PROGRESS', '2026-05-13'),
    (20, 13, 'Пневмопрес не тримає тиск', 75, 85, 95, 84.0, 'COMPLETED', '2026-05-20');

INSERT INTO service_record (id, request_id, specialist_id, work_description, completion_date, time_spent) VALUES
    (1, 3, 1, 'Замінено систему охолодження', '2026-01-14', 120),
    (2, 1, 2, 'Діагностика двигуна', '2026-01-09', 45),
    (3, 4, 4, 'Заміна різців та налагодження інструменту', '2026-01-16', 90),
    (4, 8, 12, 'Заміна фільтрів та ремонт компресора', '2026-02-15', 180),
    (5, 2, 1, 'Планове ТО змащення вузлів верстата', '2026-01-11', 60),
    (6, 12, 9, 'Чищення пальника та заміна свічки', '2026-03-19', 75),
    (7, 6, 8, 'Регулювання крюкової підвіски', '2026-02-08', 100),
    (8, 16, 19, 'Заміна ТЕН-ів та ремонт регулятора', '2026-04-17', 150),
    (9, 11, 5, 'Заміна шліфувального круга', '2026-03-13', 40),
    (10, 5, 3, 'Ремонт регулятора зварювального струму', '2026-02-05', 110),
    (11, 9, 2, 'Заміна акумулятора та ремонт автозапуску', '2026-03-06', 95),
    (12, 13, 16, 'Заміна свердлильного патрона', '2026-04-04', 55),
    (13, 15, 12, 'Ремонт компресорного блоку', '2026-04-12', 165),
    (14, 7, 18, 'Заміна сальника гідроциліндра', '2026-02-11', 130),
    (15, 17, 13, 'Чищення фільтрів та заміна щіток', '2026-05-04', 70),
    (16, 10, 5, 'Налагодження свердлильної головки', '2026-03-09', 50),
    (17, 14, 8, 'Заміна гальмівних колодок', '2026-04-07', 85),
    (18, 20, 1, 'Герметизація пневмогідравліки', '2026-05-21', 140),
    (19, 19, 9, 'Діагностика та ремонт блоку автоматики', '2026-05-15', 105),
    (20, 18, 4, 'Відновлення точність токарного верстата', '2026-05-10', 160);
