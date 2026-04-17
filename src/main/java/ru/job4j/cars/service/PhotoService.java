package ru.job4j.cars.service;

import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Photo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PhotoService {

    Photo save(PhotoDto photoDto);

    List<PhotoDto> findAllFiles();

    Optional<PhotoDto> getFileById(int id);

    boolean deleteById(int id);
}
