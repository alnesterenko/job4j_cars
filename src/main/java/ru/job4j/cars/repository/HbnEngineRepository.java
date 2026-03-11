package ru.job4j.cars.repository;

import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ThreadSafe
@Repository
@RequiredArgsConstructor
public class HbnEngineRepository implements EngineRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Engine> findAll() {
        return crudRepository.query("SELECT e FROM Engine AS e ORDER BY e.id ASC", Engine.class);
    }

    @Override
    public Optional<Engine> findById(Integer id) {
        return crudRepository.optional("FROM Engine AS e WHERE e.id = :id", Engine.class, Map.of("id", id));
    }

    @Override
    public Optional<Engine> findByName(String name) {
        return crudRepository.optional("FROM Engine AS e WHERE e.name = :name", Engine.class, Map.of("name", name));
    }

    @Override
    public Engine add(Engine engine) {
        crudRepository.run(session -> session.save(engine));
        return engine;
    }

    @Override
    public boolean replace(Integer id, Engine engine) {
        engine.setId(id);
        return crudRepository.tx(session -> session.merge(engine)) != null;
    }

    @Override
    public boolean delete(Integer id) {
        int updatedLines = crudRepository.run(
                "DELETE Engine WHERE id = :id",
                Map.of("id", id)
        );
        return updatedLines > 0;
    }

    @Override
    public void clearRepository() {
        var engines = findAll();
        for (Engine oneEngine : engines) {
            delete(oneEngine.getId());
        }
    }
}
