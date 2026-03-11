package ru.job4j.cars.repository;

import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ThreadSafe
@Repository
@RequiredArgsConstructor
public class HbnOwnerRepository implements OwnerRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Owner> findAll() {
        return crudRepository.query("SELECT DISTINCT o FROM Owner o "
                + "JOIN FETCH o.user "
                + "ORDER BY o.id ASC", Owner.class);
    }

    @Override
    public Optional<Owner> findById(Integer id) {
        return crudRepository.optional("SELECT DISTINCT o FROM Owner o "
                + "JOIN FETCH o.user "
                + "WHERE o.id = :id", Owner.class, Map.of("id", id));
    }

    @Override
    public Optional<Owner> findByName(String name) {
        return crudRepository.optional("SELECT DISTINCT o FROM Owner o "
                + "JOIN FETCH o.user "
                + "WHERE o.name = :name", Owner.class, Map.of("name", name));
    }

    @Override
    public Optional<Owner> findByUserId(Integer userId) {
        return crudRepository.optional("SELECT DISTINCT o FROM Owner o "
                + "JOIN FETCH o.user "
                + "WHERE o.user.id = :userId", Owner.class, Map.of("userId", userId));
    }

    @Override
    public Owner add(Owner owner) {
        crudRepository.run(session -> session.save(owner));
        return owner;
    }

    @Override
    public boolean delete(Integer id) {
        int updatedLines = crudRepository.run(
                "DELETE Owner WHERE id = :id",
                Map.of("id", id)
        );
        return updatedLines > 0;
    }

    @Override
    public void clearRepository() {
        var owners = findAll();
        for (Owner oneOwner : owners) {
            delete(oneOwner.getId());
        }
    }
}
