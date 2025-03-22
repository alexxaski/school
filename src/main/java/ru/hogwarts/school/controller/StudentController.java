package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/create")
    public void create(@RequestParam("/name") String name,@RequestParam("/age") int age) {
        studentService.create(new Student (name, age));
    }

    @GetMapping("/{id}")
    public Optional<Student> read(@PathVariable Long id) {
        return studentService.read(id);
    }

    @PutMapping("/update")
    public void update(@RequestBody Student updatedStudent) {
        studentService.update(updatedStudent);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
    @GetMapping("/getAll")
    public List<Student> getStudents() {
        return studentService.getAllStudents();
    }
    @GetMapping("/students/age")
    public List<Student> getStudentsByAgeRange(@RequestParam int min, @RequestParam int max) {
        return studentService.findByAgeBetween(min, max);
    }
}