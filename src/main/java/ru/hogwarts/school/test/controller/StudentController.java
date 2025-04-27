package ru.hogwarts.school.test.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.test.model.Avatar;
import ru.hogwarts.school.test.model.Student;
import ru.hogwarts.school.test.service.StudentService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController

@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;


    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
                         // TASK1
                         @GetMapping("/print-parallel")
                         public void printParallel() {
                             List<Student> students = studentService.getAllStudents();

                             // Первая группа (два студента) выводится в основном потоке
                             for (int i = 0; i < Math.min(2, students.size()); i++) {
                                 System.out.println(students.get(i).getName());
                             }

                             ExecutorService executorService = Executors.newFixedThreadPool(2);

                             // Вторая группа (третий и четвертый студент) выводится в параллельном потоке
                             Runnable secondGroupRunnable = () -> {
                                 for (int i = 2; i < Math.min(4, students.size()); i++) {
                                     System.out.println(students.get(i).getName());
                                 }
                             };
                             executorService.submit(secondGroupRunnable);

                             // Третья группа (пятый и шестой студент) выводится в другом параллельном потоке
                             Runnable thirdGroupRunnable = () -> {
                                 for (int i = 4; i < Math.min(6, students.size()); i++) {
                                     System.out.println(students.get(i).getName());
                                 }
                             };
                             executorService.submit(thirdGroupRunnable);

                             executorService.shutdown();
                         }

                        //TASK2
// Синхронизированный метод печати
                        synchronized void printSynchronized(String name) {
                            System.out.println(name);
                        }

    @GetMapping("/print-synchronized")
    public void printSynchronized() {
        List<Student> students = studentService.getAllStudents();

        // Первая группа (два студента) выводится в основном потоке
        for (int i = 0; i < Math.min(2, students.size()); i++) {
            printSynchronized(students.get(i).getName());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Вторая группа (третий и четвертый студент) выводится в параллельном потоке
        Runnable secondGroupRunnable = () -> {
            for (int i = 2; i < Math.min(4, students.size()); i++) {
                printSynchronized(students.get(i).getName());
            }
        };
        executorService.submit(secondGroupRunnable);

        // Третья группа (пятый и шестой студент) выводится в другом параллельном потоке
        Runnable thirdGroupRunnable = () -> {
            for (int i = 4; i < Math.min(6, students.size()); i++) {
                printSynchronized(students.get(i).getName());
            }
        };
        executorService.submit(thirdGroupRunnable);

        executorService.shutdown();
    }



    @GetMapping("/average-age")
    public double AverageAge() {
        return studentService.calculateAverageAge();
    }


    @GetMapping("/names-starting-with-a")
    public List<String> getStudentNamesStartingWithA() {
        return studentService.findStudentNamesStartingWithA();
    }

    @PostMapping("/create")
    public Student create(@RequestBody Student student) {
        studentService.create(student);
        return student;
    }

    @GetMapping("/{id}")
    public Student read(@PathVariable Long id) {
        return studentService.read(id).orElse(null);
    }

    @PutMapping("/update")
    public void update(@RequestBody Student updatedStudent) {
        studentService.update(updatedStudent);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }

    @GetMapping("/getAll")
    public List<Student> getStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/age")
    public List<Student> getStudentsByAgeRange(@RequestParam int min, @RequestParam int max) {
        return studentService.findByAgeBetween(min, max);
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String>

    uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() >1024 * 300){
            return ResponseEntity.badRequest().body("File is too big");
        }

        studentService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/add")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        Student savedStudent = studentService.create(student);
        return ResponseEntity.ok(savedStudent);
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatarPreview(@PathVariable Long id) {
        Optional<Avatar> optionalAvatar = studentService.findAvatar(id);
        if (optionalAvatar.isPresent()) {
            Avatar avatar = optionalAvatar.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
            headers.setContentLength(avatar.getData().length);
            return ResponseEntity.ok().headers(headers).body(avatar.getData());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/avatar")
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<Avatar> optionalAvatar = studentService.findAvatar(id);
        if (optionalAvatar.isPresent()) {
            Avatar avatar = optionalAvatar.get();
            Path path = Path.of(avatar.getFilePath());
            try (InputStream is = Files.newInputStream(path);
                 OutputStream os = response.getOutputStream();) {
                response.setStatus(200);
                response.setContentType(avatar.getMediaType());
                response.setContentLength((int) avatar.getFileSize());
                is.transferTo(os);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Avatar not found");
        }
    }
    @GetMapping("/count")
    public Long getCountOfStudents() {
        return studentService.countStudents();
    }

    @GetMapping("/average-age")
    public Double getAverageAge() {
        return studentService.averageAge();
    }

    @GetMapping("/last-five")
    public List<Student> getLastFiveStudents() {
        return studentService.findLastFiveStudents();
    }
}

