package ru.hogwarts.school.test;

import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerMVC_Test {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FacultyRepository facultyRepository;
    @SpyBean
    private FacultyService facultyService;
    @InjectMocks
    private FacultyController facultyController;

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

        mockMvc.perform(get("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    void updateFacultyTest() throws Exception {
        final Long id = 1L;
        final String updatedName = "Updated Alex";
        final String updatedColor = "Red";

        Faculty updatedFaculty = new Faculty(updatedName, updatedColor);
        updatedFaculty.setId(id);
        when(facultyRepository.existsById(anyLong())).thenReturn(true);
        when(facultyRepository.save(updatedFaculty)).thenReturn(updatedFaculty);

        JSONObject facultyJson = new JSONObject();
        facultyJson.put("id", id);
        facultyJson.put("name", updatedName);
        facultyJson.put("color", updatedColor);
        when(facultyRepository.existsById(anyLong())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/update")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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

        mockMvc.perform(get("/faculty/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(faculties.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(faculties.get(0).getName()))
                .andExpect(jsonPath("$[0].color").value(faculties.get(0).getColor()))
                .andExpect(jsonPath("$[1].name").value(faculties.get(1).getName()))
                .andExpect(jsonPath("$[1].color").value(faculties.get(1).getColor()));
    }

    @Test
    void searchFacultiesTest() throws Exception {
        final String query = "Green";

        final Long id1 = 1L;
        final String name1 = "Alex";
        final String color1 = "Green";

        Faculty faculty = new Faculty(name1, color1);
        faculty.setId(id1);

        List<Faculty> faculties = Arrays.asList(faculty);

        when(facultyRepository.findByNameOrColor(query)).thenReturn(faculties);

        mockMvc.perform(get("/faculty/faculties/search").param("query", query))
                .andExpect(status().isOk());

    }
}