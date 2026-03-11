package ru.job4j.cars.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.configuration.HibernateConfiguration;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HbnEngineRepositoryTest {

    private static EngineRepository engineRepository;

    private static Engine firstEngine = new Engine("В-2-34");
    private static Engine secondEngine = new Engine("ГТД-1250");

    @BeforeAll
    public static void initRepository() {
        engineRepository = new HbnEngineRepository(new CrudRepository(new HibernateConfiguration().sf()));
        engineRepository.clearRepository();
    }

    @AfterEach
    public void clearPriorities() {
        engineRepository.clearRepository();
    }

    /* Тестируем add() */
    @Test
    public void whenSaveThenGetSame() {
        List<Engine> listBeforeAdd = engineRepository.findAll();
        Engine engineAfterAdd = engineRepository.add(firstEngine);
        List<Engine> listAfterAdd = engineRepository.findAll();
        assertThat(firstEngine).isEqualTo(engineAfterAdd);
        assertThat(listBeforeAdd.contains(firstEngine)).isFalse();
        assertThat(listAfterAdd.contains(engineAfterAdd)).isTrue();
        assertThat(listAfterAdd.size()).isGreaterThan(listBeforeAdd.size());
    }

    @Test
    public void whenSaveTwiceThenGetException() {
        Exception exception = assertThrows(ConstraintViolationException.class, () -> {
            engineRepository.add(firstEngine);
            engineRepository.add(firstEngine);
        });
        assertThat("could not execute statement").isEqualTo(exception.getMessage());
    }

    /* Тестируем findById() */
    @Test
    public void whenFindByIdSuccess() {
        engineRepository.add(firstEngine);
        engineRepository.add(secondEngine);
        Optional<Engine> optionalEngine1 = engineRepository.findById(firstEngine.getId());
        Optional<Engine> optionalEngine2 = engineRepository.findById(secondEngine.getId());
        assertThat(optionalEngine1.isPresent()).isTrue();
        assertThat(optionalEngine1.get()).isEqualTo(firstEngine);
        assertThat(optionalEngine2.isPresent()).isTrue();
        assertThat(optionalEngine2.get()).isEqualTo(secondEngine);
    }

    @Test
    public void whenTryFindByWrongIdThenGetNoting() {
        engineRepository.add(firstEngine);
        engineRepository.add(secondEngine);
        Optional<Engine> optionalEngine = engineRepository.findById(secondEngine.getId() + 31);
        assertThat(optionalEngine.isPresent()).isFalse();
    }

    /* Тестируем findByName() */
    @Test
    public void whenFindByNameSuccess() {
        engineRepository.add(firstEngine);
        engineRepository.add(secondEngine);
        Optional<Engine> optionalEngine1 = engineRepository.findByName(firstEngine.getName());
        Optional<Engine> optionalEngine2 = engineRepository.findByName(secondEngine.getName());
        assertThat(optionalEngine1.isPresent()).isTrue();
        assertThat(optionalEngine1.get()).isEqualTo(firstEngine);
        assertThat(optionalEngine2.isPresent()).isTrue();
        assertThat(optionalEngine2.get()).isEqualTo(secondEngine);
    }

    @Test
    public void whenTryFindByWrongNameThenGetNoting() {
        engineRepository.add(firstEngine);
        engineRepository.add(secondEngine);
        Optional<Engine> optionalEngine = engineRepository.findByName(firstEngine.getName() + "-85");
        assertThat(optionalEngine.isPresent()).isFalse();
    }

    /* Тестируем replace() */
    @Test
    public void whenReplaceOneEngineSuccess() {
        engineRepository.add(firstEngine);
        Engine thirdEngine = new Engine("Реактивный прямоточный");
        engineRepository.replace(firstEngine.getId(), thirdEngine);
        Optional<Engine> optionalEngine1 = engineRepository.findByName(firstEngine.getName());
        Optional<Engine> optionalEngine3 = engineRepository.findByName(thirdEngine.getName());
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList.size()).isEqualTo(1);
        assertThat(optionalEngine1.isEmpty()).isTrue();
        assertThat(optionalEngine3.isPresent()).isTrue();
        assertThat(optionalEngine3.get()).isEqualTo(thirdEngine).isEqualTo(engineList.get(0));
    }

    /* Тестируем delete() */
    @Test
    public void whenSaveOneThenDeleteIt() {
        Engine engineAfterAdd = engineRepository.add(firstEngine);
        List<Engine> listAfterAdd = engineRepository.findAll();
        boolean success = engineRepository.delete(engineAfterAdd.getId());
        List<Engine> listAfterDelete = engineRepository.findAll();
        assertThat(listAfterAdd.size()).isGreaterThan(listAfterDelete.size());
        assertThat(listAfterDelete.contains(engineAfterAdd)).isFalse();
        assertThat(success).isTrue();
    }

    @Test
    public void whenTryDeleteTaskUsingWrongId() {
        Engine engineAfterAdd = engineRepository.add(firstEngine);
        List<Engine> listAfterAdd = engineRepository.findAll();
        boolean success = engineRepository.delete(engineAfterAdd.getId() + 31);
        List<Engine> listAfterDeleteFail = engineRepository.findAll();
        assertThat(listAfterAdd.size()).isEqualTo(listAfterDeleteFail.size());
        assertThat(listAfterDeleteFail.contains(engineAfterAdd)).isTrue();
        assertThat(success).isFalse();
    }

}