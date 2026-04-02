package ru.job4j.cars.repository;

import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Photo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ThreadSafe
@Repository
@RequiredArgsConstructor
public class HbnPhotoRepository implements PhotoRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Photo> findAll() {
        return crudRepository.query("FROM Photo ORDER BY id ASC", Photo.class);
    }

    @Override
    public Optional<Photo> findById(Integer id) {
        return crudRepository.optional("FROM Photo AS ph WHERE ph.id = :id", Photo.class, Map.of("id", id));
    }

    @Override
    public Photo add(Photo photo) {
        crudRepository.run(session -> session.save(photo));
        return photo;
    }

    @Override
    public boolean replace(Integer id, Photo photo) {
        return crudRepository.run("UPDATE Photo SET name = :name WHERE id = :id",
                Map.of("id", id, "name", photo.getName())) > 0;
    }

    @Override
    public boolean delete(Integer id) {
        return crudRepository.run("DELETE Photo WHERE id = :id",
                Map.of("id", id)) > 0;
    }

    @Override
    public void clearRepository() {
        var photos = findAll();
        for (Photo onePhoto : photos) {
            delete(onePhoto.getId());
        }
    }
}
