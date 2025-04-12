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
import ru.hogwarts.school.test.controller.FacultyController;
import ru.hogwarts.school.test.controller.StudentController;
import ru.hogwarts.school.test.model.Faculty;
import ru.hogwarts.school.test.model.Student;
import ru.hogwarts.school.test.repository.AvatarRepository;
import ru.hogwarts.school.test.repository.FacultyRepository;
import ru.hogwarts.school.test.repository.StudentRepository;
import ru.hogwarts.school.test.service.FacultyService;
import ru.hogwarts.school.test.service.StudentService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerMVC_Test {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyRepository studentRepository;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @MockitoBean
    private FacultyService studentService;

    @Test
    void addFacultyTest() throws Exception {
final String color = "Green";
final String name = "Alex";
        JSONObject facultyJson = new JSONObject();
        facultyJson.put("name", "name");
        facultyJson.put("color", "color");

        Faculty savedFaculty = new Faculty("name", "color");
        when(facultyRepository.save(savedFaculty)).thenReturn(savedFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty/create")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.color").value("color"));
    }
    @Test
    void readFacultyTest() throws Exception {
        final Long id = 1L;
        final String name = "Alex";
        final String color = "Green";

        Faculty faculty = new Faculty(name, color);
        faculty.setId(id);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.color").doesNotExist());
    }
    @Test
    void updateFacultyTest() throws Exception {
        final Long id = 1L;
        final String updatedName = "Updated Alex";
        final String updatedColor = "Red";

        Faculty updatedFaculty = new Faculty(updatedName, updatedColor);
        updatedFaculty.setId(id);

        when(facultyRepository.save(updatedFaculty)).thenReturn(updatedFaculty);

        JSONObject facultyJson = new JSONObject();
        facultyJson.put("id", id);
        facultyJson.put("name", updatedName);
        facultyJson.put("color", updatedColor);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/update")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.color").doesNotExist());
    }
    @Test
    void deleteFacultyTest() throws Exception {
        final Long id = 1L;

        willDoNothing().given(facultyRepository).deleteById(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/delete/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void getAllFacultiesTest() throws Exception {
        final Long id1 = 1L;
        final String name1 = "Alex";
        final String color1 = "Green";

        final Long id2 = 2L;
        final String name2 = "Bob";
        final String color = "Blue";


        List<Faculty> faculties = Arrays.asList(
                new Faculty(name1, color1),
                new Faculty(name2, color)
        );

        when(facultyRepository.findAll()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].name").doesNotExist())
                .andExpect(jsonPath("$[0].color").doesNotExist())
                .andExpect(jsonPath("$[1].name").doesNotExist())
                .andExpect(jsonPath("$[1].color").doesNotExist());
    }
    @Test
    void searchFacultiesTest() throws Exception {
        final String query = "Green";

        final Long id1 = 1L;
        final String name1 = "Alex";
        final String color1 = "Green";

        List<Faculty> faculties = Arrays.asList(
                new Faculty(name1, color1)
        );

        when(facultyRepository.findByNameOrColor(query)).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/faculties/search")
                        .param("query", query)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").doesNotExist())
                .andExpect(jsonPath("$[0].color").doesNotExist());
    }
}
