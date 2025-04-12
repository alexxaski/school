package ru.hogwarts.school.test.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.test.model.Avatar;
import ru.hogwarts.school.test.model.Student;
import ru.hogwarts.school.test.repository.AvatarRepository;
import ru.hogwarts.school.test.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public StudentService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    public Student create(Student student) {
        if (student == null || student.getId() != null) {
            throw new IllegalArgumentException("студент уже имеет ID или является пустым");
        }
        studentRepository.save(student);
        return student;
    }
    public Student save(Student student) {
        Student savedStudent = studentRepository.save(student);
        return savedStudent;
    }

    public Optional<Student> read(Long id) {
        return studentRepository.findById(id);
    }

    public void update(Student updateStudent) {
        if (!studentRepository.existsById(updateStudent.getId())) {
            throw new IllegalArgumentException("Невозможно добавить");
        }
        studentRepository.save(updateStudent);
    }
    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(studentRepository.findAll());
    }
    public List<Student> findByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    public Optional<Avatar> findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId);
    }
@Transactional
    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
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