package ru.hogwarts.school.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.test.model.Faculty;
import ru.hogwarts.school.test.service.FacultyService;

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
    public Faculty create(@RequestBody Faculty faculty) {
        facultyService.create(faculty);
        return faculty;
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
    @GetMapping("/faculties/search")
    public List<Faculty> getFacultiesByNameOrColor(@RequestParam String query) {
        return facultyService.findByNameOrColor(query);
    }
}