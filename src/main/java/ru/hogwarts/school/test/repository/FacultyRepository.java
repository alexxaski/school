package ru.hogwarts.school.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.test.model.Faculty;

import java.util.List;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    @Query("SELECT f FROM Faculty f WHERE LOWER(f.name) LIKE %?1% OR LOWER(f.color) LIKE %?1%")
    List<Faculty> findByNameOrColor(String query);
}