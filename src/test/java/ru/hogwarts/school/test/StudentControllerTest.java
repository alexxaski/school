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
import ru.hogwarts.school.test.model.Student;
import ru.hogwarts.school.test.repository.FacultyRepository;
import javax.sound.sampled.Port;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StudentControllerTest {

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
        assertThat(studentController).isNotNull();
    }

    @Test
    public void testCreateStudent() {

        Student student = new Student("John Doe", 20);

        ResponseEntity response = restTemplate.exchange("http://localhost:" + port + "/student/create",
                HttpMethod.POST,
                new HttpEntity<>(student),
                Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void testReadStudent() {
        Student student = new Student("John Doe", 20);
        studentController.create(student);

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/student/" + student.getId(), String.class))
                .contains(student.getId().toString())
                .contains((student.getName()));
    }

    @Test
    public void testUpdateStudent() {
        Student updatedStudent = new Student("Jane Doe", 21);
        studentController.create(updatedStudent);

        updatedStudent.setName("Alex");

        ResponseEntity response = restTemplate.exchange("/http://localhost:" + port + "/student/",
                HttpMethod.PUT, new HttpEntity<>(updatedStudent), String.class);
        assertThat(response.toString().contains("Alex"));
    }

    @Test
    public void testDeleteStudent() {
        Student student = new Student("Jane Doe", 21);
        studentController.create(student);

        ResponseEntity response = restTemplate.exchange("/http://localhost:" + port + "/student/delete/" + student.getId(),
                HttpMethod.DELETE, new HttpEntity<>(student.getId()), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testFilterStudentsByAge() {
        Student student = new Student("John Doe", 20);
        studentController.create(student);

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/student/age?min=19&max=29", String.class))
                .contains(student.getId().toString())
                .contains((student.getName()));
    }
}