package ru.hogwarts.school.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.test.model.Avatar;
import ru.hogwarts.school.test.repository.AvatarRepository;

@Service
public class AvatarService {

    @Autowired
    private AvatarRepository avatarRepository;

    public Page<Avatar> findAll(Pageable pageable) {
        return avatarRepository.findAll(pageable);
    }
}