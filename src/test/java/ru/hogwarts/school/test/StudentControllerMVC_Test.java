package ru.hogwarts.school.test;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.test.controller.StudentController;
import ru.hogwarts.school.test.model.Avatar;
import ru.hogwarts.school.test.model.Faculty;
import ru.hogwarts.school.test.model.Student;
import ru.hogwarts.school.test.repository.AvatarRepository;
import ru.hogwarts.school.test.repository.FacultyRepository;
import ru.hogwarts.school.test.repository.StudentRepository;
import ru.hogwarts.school.test.service.StudentService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerMVC_Test {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private AvatarRepository avatarRepository;
    @SpyBean
    private StudentService studentService;
    @InjectMocks
    private StudentController studentController;

    @Test
    void createStudentTest() throws Exception {
        final String name = "Alex";
        final int age = 20;

        JSONObject studentJson = new JSONObject();
        studentJson.put("name", name);
        studentJson.put("age", age);

        Student savedStudent = new Student(name, age);
        when(studentRepository.save(savedStudent)).thenReturn(savedStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student/create")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }
    @Test
    void readStudentTest() throws Exception {
        final Long id = 1L;
        final String name = "Alex";
        final int age = 20;

        Student student = new Student(name, age);
        student.setId(id);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.age").value(student.getAge()))
                .andExpect(jsonPath("$.name").value(student.getName()));
    }
    @Test
    void updateStudentTest() throws Exception {
        final Long id = 1L;
        final String updatedName = "Updated Alex";
        final int updatedAge = 21;

        Student updatedStudent = new Student(updatedName, updatedAge);
        updatedStudent.setId(id);
        when(studentRepository.existsById(anyLong())).thenReturn(true);
        when(studentRepository.save(updatedStudent)).thenReturn(updatedStudent);

        JSONObject studentJson = new JSONObject();
        studentJson.put("id", id);
        studentJson.put("name", updatedName);
        studentJson.put("age", updatedAge);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/update")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

      @Test
      void deleteStudentTest() throws Exception {
          final Long id = 1L;

        willDoNothing().given(studentRepository).deleteById(id);

         mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/delete/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
      }
    @Test
    void getAllStudentsTest() throws Exception {
        final Long id1 = 1L;
        final String name1 = "Alex";
        final int age1 = 20;

        final Long id2 = 2L;
        final String name2 = "Bob";
        final int age2 = 25;

        List<Student> students = Arrays.asList(
                new Student(name1, age1),
                new Student(name2, age2)
        );

        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(students.get(0).getName()))
                .andExpect(jsonPath("$[0].age").value(students.get(0).getAge()))
                .andExpect(jsonPath("$[1].name").value(students.get(1).getName()))
                .andExpect(jsonPath("$[1].age").value(students.get(1).getAge()));
    }
    @Test
    void getStudentsByAgeRangeTest() throws Exception {
        final int minAge = 18;
        final int maxAge = 25;

        final Long id1 = 1L;
        final String name1 = "Alex";
        final int age1 = 20;

        final Long id2 = 2L;
        final String name2 = "Bob";
        final int age2 = 24;

        List<Student> students = Arrays.asList(
                new Student(name1, age1),
                new Student(name2, age2)
        );

        when(studentRepository.findByAgeBetween(minAge, maxAge)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/age?min={min}&max={max}", minAge, maxAge)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(students.get(0).getName()))
                .andExpect(jsonPath("$[0].age").value(students.get(0).getAge()))
                .andExpect(jsonPath("$[1].name").value(students.get(1).getName()))
                .andExpect(jsonPath("$[1].age").value(students.get(1).getAge()));
    }
}
