package ru.job4j.cars.repository;

import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ThreadSafe
@Repository
@RequiredArgsConstructor
public class HbnPostRepository implements PostRepository {

    private final CrudRepository crudRepository;

    private final static String BODY_OF_HQL_QUERY =
            "SELECT DISTINCT p "
                    + "FROM Post p "
                    + "LEFT JOIN FETCH p.user pu "
                    + "LEFT JOIN FETCH p.car c "
                    + "LEFT JOIN FETCH c.engine e "
                    + "LEFT JOIN FETCH c.owner o "
                    + "LEFT JOIN FETCH o.user u "
                    + "LEFT JOIN FETCH p.photos pphotos ";

    @Override
    public List<Post> findAll() {
        return crudRepository.query(
                BODY_OF_HQL_QUERY
                        + "ORDER BY p.id ASC", Post.class);
    }

    @Override
    public Optional<Post> findById(Integer id) {
        Optional<Post> resultOptional = Optional.empty();
        if (id != null) {
            resultOptional = crudRepository.optional(
                    BODY_OF_HQL_QUERY
                            + "WHERE p.id = :id",
                    Post.class,
                    Map.of("id", id));
        }
        return resultOptional;
    }

    @Override
    public List<Post> findAllByDescription(String description) {
        List<Post> resultList = new ArrayList<>();
        if (description != null &&  !description.trim().isEmpty()) {
            resultList = crudRepository.query(
                    BODY_OF_HQL_QUERY
                            + "WHERE p.description LIKE :description",
                    Post.class,
                    Map.of("description", description));
        }
        return resultList;
    }

    @Override
    public List<Post> findAllByDescription(String description, boolean like) {
            if (like) {
                description = "%" + description + "%";
            }
        return findAllByDescription(description);
    }

    @Override
    public Optional<Post> findByCar(Car car) {
        Optional<Post> resultOptional = Optional.empty();
        if (car != null) {
            resultOptional = crudRepository.optional(
                    BODY_OF_HQL_QUERY
                            + "WHERE p.car = :car",
                    Post.class,
                    Map.of("car", car));
        }
        return resultOptional;
    }

    @Override
    public List<Post> findAllByUser(User user) {
        List<Post> resultList = new ArrayList<>();
        if (user != null) {
            resultList = crudRepository.query(
                    BODY_OF_HQL_QUERY
                            + "WHERE p.user = :user",
                    Post.class,
                    Map.of("user", user));
        }
        return resultList;
    }

    @Override
    public List<Post> showAllForLastDay() {
        LocalDateTime since = LocalDateTime.now(ZoneId.of("UTC")).minusDays(1);
            return crudRepository.query(
                    BODY_OF_HQL_QUERY
                            + "WHERE p.created >= :since",
                    Post.class,
                    Map.of("since", since));
    }

    @Override
    public List<Post> showAllWhereArePhoto() {
        return crudRepository.query(
                BODY_OF_HQL_QUERY
                        + "WHERE EXISTS (SELECT 1 FROM Photo ph where ph.post = p)",
                Post.class);
    }

    @Override
    public List<Post> showAllByCarName(String name) {
        List<Post> resultList = new ArrayList<>();
        if (name != null &&  !name.trim().isEmpty()) {
            resultList = crudRepository.query(
                    BODY_OF_HQL_QUERY
                            + "WHERE c.name LIKE :name",
                    Post.class,
                    Map.of("name", name));
        }
        return resultList;
    }

    @Override
    public List<Post> showAllByCarName(String name, boolean like) {
        if (like) {
            name = "%" + name + "%";
        }
        return showAllByCarName(name);
    }

    @Override
    public Post add(Post post) {
        crudRepository.run(session -> session.save(post));
        return post;
    }

    @Override
    public boolean replace(Integer id, Post post) {
        post.setId(id);
        return crudRepository.tx(session -> session.merge(post)) != null;
    }

    @Override
    public boolean delete(Integer id) {
        int updatedLines = crudRepository.run(
                "DELETE Post WHERE id = :id",
                Map.of("id", id)
        );
        return updatedLines > 0;
    }

    @Override
    public void clearRepository() {
        var posts = findAll();
        for (Post onePost : posts) {
            delete(onePost.getId());
        }
    }
}
