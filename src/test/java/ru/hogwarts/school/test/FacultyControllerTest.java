package ru.hogwarts.school.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.test.controller.StudentController;
import ru.hogwarts.school.test.model.Faculty;
import ru.hogwarts.school.test.model.Student;
import ru.hogwarts.school.test.repository.FacultyRepository;

import javax.sound.sampled.Port;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FacultyControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentController studentController;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoads() {
        assertThat(facultyRepository).isNotNull();
    }

    @Test
    public void testCreateFaculty() {

        Faculty faculty= new Faculty("John Doe","RED");

        ResponseEntity response = restTemplate.exchange("http://localhost:" + port + "/faculty/create",
                HttpMethod.POST,
                new HttpEntity<>(faculty),
                Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void testReadFaculty() {
        Faculty faculty = new Faculty("John Doe", "RED");
        facultyRepository.save(faculty);

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/faculty/" + faculty.getId(), String.class))
                .contains(faculty.getId().toString())
                .contains((faculty.getName()));
    }

    @Test
    public void testUpdateFaculty() {
        Faculty originalFaculty = new Faculty("Jane Doe", "RED");
        facultyRepository.save(originalFaculty);

        Faculty updatedFaculty = new Faculty(originalFaculty.getColor(),"BLUE" );

        ResponseEntity response = restTemplate.exchange("http://localhost:" + port + "/faculty/",
                HttpMethod.PUT, new HttpEntity<>(updatedFaculty), String.class);
        assertThat(response.toString().contains("BLUE"));
    }

    @Test
    public void testDeleteFaculty() {
        Faculty faculty = new Faculty("Jane Doe", "RED");
        facultyRepository.save(faculty);

        ResponseEntity response = restTemplate.exchange("http://localhost:" + port + "/faculty/delete/" + faculty.getId(),
                HttpMethod.DELETE, new HttpEntity<>(faculty.getId()), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testFilterFacultysColor() {
        Faculty faculty = new Faculty("John Doe", "BLACK");
        facultyRepository.save(faculty);

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/faculty/color?color=BLACK",
                String.class))
                .contains(faculty.getId().toString());
    }
}
