package ru.hogwarts.school.test.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.test.model.Avatar;
import ru.hogwarts.school.test.model.Student;
import ru.hogwarts.school.test.repository.AvatarRepository;
import ru.hogwarts.school.test.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public double calculateAverageAge() {
        return studentRepository.findAll().parallelStream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }


    public List<String> findStudentNamesStartingWithA() {
        return studentRepository.findAll().parallelStream()
                .map(Student::getName)
                .filter(name -> name.startsWith("A"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }

    public Long countStudents() {
        log.info("Получаю общее количество студентов.");
        return studentRepository.countStudents();
    }

    public Double averageAge() {
        log.info("Вычисляю средний возраст студентов.");
        return studentRepository.averageAge();
    }

    public List<Student> findLastFiveStudents() {
        log.info("Получаю список пяти последних студентов.");
        return studentRepository.findLastFiveStudents();
    }

    public Student create(Student student) {
        log.info("Создаю нового студента.");
        return studentRepository.save(student);
    }

    public Optional<Student> read(Long id) {
        log.info("Получаю информацию о студенте с id={}.", id);
        return studentRepository.findById(id);
    }

    public void update(Student updatedStudent) {
        log.info("Обновляю информацию о студенте с id={}.", updatedStudent.getId());
        studentRepository.save(updatedStudent);
    }

    public void delete(Long id) {
        log.info("Удаляю студента с id={}.", id);
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        log.info("Получаю список всех студентов.");
        return Collections.unmodifiableList(studentRepository.findAll());
    }

    public List<Student> findByAgeBetween(int min, int max) {
        log.info("Получаю список студентов возрастом от {} до {}.", min, max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public Optional<Avatar> findAvatar(Long studentId) {
        log.info("Получаю аватар студента с id={}.", studentId);
        return avatarRepository.findByStudentId(studentId);
    }
@Transactional
    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
    log.info("Загружаю аватар студента с id={}.", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Студент с таким ID не существует"));

        // Ограничение максимального размера файла
        if (file.getSize() > 1024 * 1024 * 5) { // Максимум 5 МБ
            throw new FileNotFoundException("Размер файла превышает допустимое значение");
        }

        // Нормализуем имя файла
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Проверяем наличие недопустимых символов
        if (fileName.contains("..")) {
            throw new FileNotFoundException("Недопустимые символы в имени файла");
        }

        // Создаем путь для хранения файла
        Path storageDirectory = Paths.get("./uploads/avatars"); // Адрес директории для хранения файлов
        Files.createDirectories(storageDirectory);

        // Копируем файл в целевую директорию
        Path targetLocation = storageDirectory.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Сохраняем метаданные файла в базу данных
        Avatar avatar = new Avatar();
        avatar.setFilePath(targetLocation.toString());
        avatar.setMediaType(file.getContentType());
        avatar.setFileSize(file.getSize());
        avatar.setStudent(student);
        avatarRepository.save(avatar);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

}