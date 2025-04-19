-- Получение информации о всех студентах вместе с названиями факультетов
SELECT s.name AS student_name, s.age, f.name AS faculty_name
FROM Student s
INNER JOIN Faculty f ON s.faculty_id = f.id;

-- Получение информации только о студентах, у которых есть аватарки
SELECT s.name AS student_name
FROM Student s
JOIN Avatar a ON s.id = a.student_id;

