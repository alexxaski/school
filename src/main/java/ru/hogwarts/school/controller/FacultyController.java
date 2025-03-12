package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    @Autowired
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping("/create")
    public void create(@RequestParam("/name") String name,@RequestParam("/color") String color) {
        facultyService.create(new Faculty(name, color));
    }

    @GetMapping("/{id}")
    public Optional<Faculty> read(@PathVariable Long id) {
        return facultyService.read(id);
    }

    @PutMapping("/update")
    public void update(@RequestBody Faculty updatedFaculty) {
        facultyService.update(updatedFaculty);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        facultyService.delete(id);
    }

    @GetMapping("/getAll")
    public List<Faculty> getFacultyies() {
        return facultyService.getAllFaculties();
    }
}