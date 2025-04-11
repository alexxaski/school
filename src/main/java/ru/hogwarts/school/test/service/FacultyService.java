package ru.hogwarts.school.test.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.test.model.Faculty;
import ru.hogwarts.school.test.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public void create(Faculty faculty) {
        if (faculty == null || faculty.getId() != null) {
            throw new IllegalArgumentException("Факультет уже имеет ID или является пустым");
        }
        facultyRepository.save(faculty);
    }

    public Optional<Faculty> read(Long id) {
        return facultyRepository.findById(id);
    }

    public void update(Faculty updateFaculty) {
        if (!facultyRepository.existsById(updateFaculty.getId())) {
            throw new IllegalArgumentException("Невозможно обновить несуществующий факультет");
        }
        facultyRepository.save(updateFaculty);
    }

    public void delete(Long id) {
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getAllFaculties() {
        return Collections.unmodifiableList(facultyRepository.findAll());
    }

    public List<Faculty> findByNameOrColor(String query) {
        return facultyRepository.findByNameOrColor(query.toLowerCase());
    }
}