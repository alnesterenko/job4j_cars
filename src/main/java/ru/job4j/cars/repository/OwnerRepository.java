package ru.job4j.cars.repository;

import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository {

    List<Owner> findAll();

    Optional<Owner> findById(Integer id);

    Optional<Owner> findByName(String name);
    
    Optional<Owner> findByUserId(Integer userId);

    /* Добавлено для удобства тестирования */

    Owner add(Owner owner);

    boolean delete(Integer id);

    void clearRepository();
}
