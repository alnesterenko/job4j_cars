package ru.job4j.cars.repository;

import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    List<Post> findAll();

    Optional<Post> findById(Integer id);

    List<Post> findAllByDescription(String description, boolean like);

    List<Post> findAllByDescription(String description);

    Optional<Post> findByCar(Car car);

    List<Post> findAllByUser(User user);

    /* Методы из задания */

    List<Post> showAllForLastDay();

    List<Post> showAllWhereArePhoto();

    List<Post> showAllByCarName(String name, boolean like);

    List<Post> showAllByCarName(String name);

    /* Добавлено для удобства тестирования */

    Post add(Post post);

    boolean replace(Integer id, Post post);

    boolean delete(Integer id);

    void clearRepository();
}
