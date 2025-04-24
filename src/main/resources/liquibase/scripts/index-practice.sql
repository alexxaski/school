-- liquibase formatted sql

-- changeset alexxaski:1
CREATE INDEX student_name_index ON students (name)

-- changeset alexxaski:2
CREATE INDEX faculty_nc_index ON faculties (name, color)