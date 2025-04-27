package ru.hogwarts.school.test.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.test.model.Faculty;
import ru.hogwarts.school.test.repository.FacultyRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public String findFacultyWithLongestName() {
        return facultyRepository.findAll().parallelStream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

    public void create(Faculty faculty) {
        log.info("Создаю новый факультет.");
        facultyRepository.save(faculty);
    }

    public Optional<Faculty> read(Long id) {
        log.info("Получаю информацию о факультете с id={}.", id);
        return facultyRepository.findById(id);
    }

    public void update(Faculty updatedFaculty) {
        log.info("Обновляю информацию о факультете с id={}.", updatedFaculty.getId());
        facultyRepository.save(updatedFaculty);
    }

    public void delete(Long id) {
        log.info("Удаляю факультет с id={}.", id);
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getAllFaculties() {
        log.info("Получаю список всех факультетов.");
        return Collections.unmodifiableList(facultyRepository.findAll());
    }

    public List<Faculty> findByNameOrColor(String query) {
        log.info("Получаю список факультетов по фильтру '{}'.", query);
        return facultyRepository.findByNameOrColor(query.toLowerCase());
    }
}