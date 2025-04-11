package ru.hogwarts.school.test;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.test.controller.StudentController;
import ru.hogwarts.school.test.model.Faculty;
import ru.hogwarts.school.test.model.Student;
import ru.hogwarts.school.test.repository.AvatarRepository;
import ru.hogwarts.school.test.repository.FacultyRepository;
import ru.hogwarts.school.test.repository.StudentRepository;
import ru.hogwarts.school.test.service.StudentService;

import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerMVC_Test {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @MockitoBean
    private AvatarRepository avatarRepository;

    @MockitoBean
    private StudentService studentService;

    @Test
    void addStudentTest() throws Exception {
        Faculty faculty = new Faculty("Faculty", "yellow");
        facultyRepository.save(faculty);

        JSONObject studentJson = new JSONObject();
        studentJson.put("name", "Student");
        studentJson.put("age", 20);
        studentJson.put("faculty", faculty.getId());

        Student savedStudent = new Student("Student", 20);
        when(studentRepository.save(savedStudent)).thenReturn(savedStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student/create")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Student"))
                .andExpect(jsonPath("$.age").value("20"));

    }
}