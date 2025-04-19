-- Таблица Человек
CREATE TABLE Person (
    person_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INTEGER CHECK (age >= 18),
    has_drivers_license BOOLEAN DEFAULT FALSE,
    car_id INTEGER,
    CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES Car(car_id)
);

-- Таблица Машина
CREATE TABLE Car (
    car_id SERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) CHECK (price > 0)
);