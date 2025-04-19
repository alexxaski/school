-- Создание или модификация таблицы Student
ALTER TABLE Student
ADD CONSTRAINT chk_age CHECK (age >= 16),
ALTER COLUMN age SET DEFAULT 20,
ADD CONSTRAINT unique_student_name UNIQUE (name);

-- Создание или модификация таблицы Faculty
ALTER TABLE Faculty
ADD CONSTRAINT unique_faculty_name_color UNIQUE (name, color);

-- Создание таблицы Faculty
CREATE TABLE Faculty (
    id SERIAL PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    color VARCHAR(25) NOT NULL,
    CONSTRAINT unique_name_color UNIQUE (name, color)
);

-- Создание таблицы Student
CREATE TABLE Student (
    id SERIAL PRIMARY KEY,
    name VARCHAR(25) NOT NULL UNIQUE,
    age INTEGER DEFAULT 20 CHECK (age >= 16),
    faculty_id INTEGER NOT NULL,
    CONSTRAINT fk_faculty FOREIGN KEY (faculty_id) REFERENCES Faculty(id)
);

-- Получение информации о всех студентах вместе с названиями факультетов
SELECT s.name AS student_name, s.age, f.name AS faculty_name
FROM Student s
INNER JOIN Faculty f ON s.faculty_id = f.id;

-- Получение информации только о студентах, у которых есть аватарки
SELECT s.name AS student_name
FROM Student s
JOIN Avatar a ON s.id = a.student_id;
