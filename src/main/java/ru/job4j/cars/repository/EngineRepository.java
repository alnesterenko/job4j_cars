package ru.job4j.cars.repository;

import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Optional;

public interface EngineRepository {

    List<Engine> findAll();

    Optional<Engine> findById(Integer id);

    Optional<Engine> findByName(String name);

    /* Добавлено для удобства тестирования */

    Engine add(Engine engine);

    boolean replace(Integer id, Engine engine);

    boolean delete(Integer id);

    void clearRepository();
}
