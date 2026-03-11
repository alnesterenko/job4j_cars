package ru.job4j.cars.repository;

import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ThreadSafe
@Repository
@RequiredArgsConstructor
public class HbnCarRepository implements CarRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Car> findAll() {
        return crudRepository.query(
                "SELECT DISTINCT c "
                        + "FROM Car c "
                        + "LEFT JOIN FETCH c.engine e "
                        + "LEFT JOIN FETCH c.owner o "
                        + "LEFT JOIN FETCH o.user u "
                        + "LEFT JOIN FETCH c.owners ho "
                        + "LEFT JOIN FETCH ho.user hu "
                        + "ORDER BY c.id ASC", Car.class);
    }

    @Override
    public Optional<Car> findById(Integer id) {
        Optional<Car> resultOptional = Optional.empty();
        if (id != null) {
            resultOptional = crudRepository.optional(
                    "SELECT DISTINCT c "
                            + "FROM Car AS c "
                            + "LEFT JOIN FETCH c.engine e "
                            + "LEFT JOIN FETCH c.owner o "
                            + "LEFT JOIN FETCH o.user u "
                            + "LEFT JOIN FETCH c.owners ho "
                            + "LEFT JOIN FETCH ho.user hu "
                            + "WHERE c.id = :id",
                    Car.class,
                    Map.of("id", id));
        }
        return resultOptional;
    }

    @Override
    public List<Car> findAllByName(String name) {
        List<Car> resultList = new ArrayList<>();
        if (name != null) {
            resultList = crudRepository.query(
                    "SELECT DISTINCT c "
                            + "FROM Car c "
                            + "LEFT JOIN FETCH c.engine e "
                            + "LEFT JOIN FETCH c.owner o "
                            + "LEFT JOIN FETCH o.user u "
                            + "LEFT JOIN FETCH c.owners ho "
                            + "LEFT JOIN FETCH ho.user hu "
                            + "WHERE c.name = :name",
                    Car.class,
                    Map.of("name", name));
        }
        return resultList;
    }

    @Override
    public List<Car> findAllByEngine(Engine engine) {
        List<Car> resultList = new ArrayList<>();
        if (engine != null) {
            resultList = crudRepository.query(
                    "SELECT DISTINCT c "
                            + "FROM Car c "
                            + "LEFT JOIN FETCH c.engine e "
                            + "LEFT JOIN FETCH c.owner o "
                            + "LEFT JOIN FETCH o.user u "
                            + "LEFT JOIN FETCH c.owners ho "
                            + "LEFT JOIN FETCH ho.user hu "
                            + "WHERE c.engine = :engine",
                    Car.class,
                    Map.of("engine", engine));
        }
        return resultList;
    }

    @Override
    public List<Car> findAllByOwner(Owner owner) {
        List<Car> resultList = new ArrayList<>();
        if (owner != null) {
            resultList = crudRepository.query(
                    "SELECT DISTINCT c "
                            + "FROM Car c "
                            + "LEFT JOIN FETCH c.engine e "
                            + "LEFT JOIN FETCH c.owner o "
                            + "LEFT JOIN FETCH o.user u "
                            + "LEFT JOIN FETCH c.owners ho "
                            + "LEFT JOIN FETCH ho.user hu "
                            + "WHERE c.owner = :owner",
                    Car.class,
                    Map.of("owner", owner));
        }
        return resultList;
    }

    @Override
    public Car add(Car car) {
        crudRepository.run(session -> session.save(car));
        return car;
    }

    @Override
    public boolean replace(Integer id, Car car) {
        car.setId(id);
        return crudRepository.tx(session -> session.merge(car)) != null;
    }

    @Override
    public boolean delete(Integer id) {
        int updatedLines = crudRepository.run(
                "DELETE Car WHERE id = :id",
                Map.of("id", id)
        );
        return updatedLines > 0;
    }

    @Override
    public void clearRepository() {
        var cars = findAll();
        for (Car oneCar : cars) {
            delete(oneCar.getId());
        }
    }
}
