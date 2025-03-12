package ru.hogwarts.school.service;


import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void create(Student student) {
        if (student == null || student.getId() != null) {
            throw new IllegalArgumentException("студент уже имеет ID или является пустым");
        }
        studentRepository.save(student);
    }


    public Optional<Student> read(Long id) {
        return studentRepository.findById(id);
    }

    public void update(Student updateStudent) {
        if (!studentRepository.existsById(updateStudent.getId())) {
            throw new IllegalArgumentException("Невозможно добавить");
        }
        studentRepository.save(updateStudent);
    }
    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(studentRepository.findAll());
    }
}

