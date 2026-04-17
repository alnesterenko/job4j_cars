package ru.job4j.cars.repository;

import ru.job4j.cars.model.Photo;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository {

    List<Photo> findAll();

    Optional<Photo> findById(Integer id);

    Photo add(Photo photo);

    boolean delete(Integer id);

    void clearRepository();
}
