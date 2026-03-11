package ru.job4j.cars.repository;

import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Owner;

import java.util.List;
import java.util.Optional;

public interface CarRepository {

    List<Car> findAll();

    Optional<Car> findById(Integer id);

    List<Car> findAllByName(String name);

    List<Car> findAllByEngine(Engine engine);

    List<Car> findAllByOwner(Owner owner);

    /* Добавлено для удобства тестирования */

    Car add(Car car);

    boolean replace(Integer id, Car car);

    boolean delete(Integer id);

    void clearRepository();
}
