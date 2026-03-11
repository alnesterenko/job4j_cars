package ru.job4j.cars.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.configuration.HibernateConfiguration;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class HbnCarRepositoryTest {

    private static CarRepository hbnCarRepository;

    private static EngineRepository hbnEngineRepository;

    private static OwnerRepository hbnOwnerRepository;

    private static UserRepository userRepository;

    private static CrudRepository crudRepository = new CrudRepository(new HibernateConfiguration().sf());

    private static Engine firstTestEngine = new Engine("Турбовальный");
    private static Engine secondTestEngine = new Engine("Реактивный");
    private static Engine thirdTestEngine = new Engine("Реактивный Прямоточный");

    private static User firstTestUser = new User("111", "111");
    private static User secondTestUser = new User("222", "222");
    private static User thirdTestUser = new User("333", "333");

    private static Owner firstTestOwner = new Owner("firstTestOwner", firstTestUser);
    private static Owner secondTestOwner = new Owner("secondTestOwner", secondTestUser);
    private static Owner thirdTestOwner = new Owner("thirdTestOwner", thirdTestUser);

    @BeforeAll
    public static void initRepository() {
        hbnCarRepository = new HbnCarRepository(crudRepository);
        hbnEngineRepository = new HbnEngineRepository(crudRepository);
        hbnOwnerRepository = new HbnOwnerRepository(crudRepository);
        userRepository = new UserRepository(new HibernateConfiguration().sf());

        userRepository.clearRepository();
        userRepository.create(firstTestUser);
        userRepository.create(secondTestUser);
        userRepository.create(thirdTestUser);

        hbnOwnerRepository.clearRepository();
        hbnOwnerRepository.add(firstTestOwner);
        hbnOwnerRepository.add(secondTestOwner);
        hbnOwnerRepository.add(thirdTestOwner);

        hbnEngineRepository.clearRepository();
        hbnEngineRepository.add(firstTestEngine);
        hbnEngineRepository.add(secondTestEngine);
        hbnEngineRepository.add(thirdTestEngine);
    }

    @AfterEach
    public void clearCars() {
        hbnCarRepository.clearRepository();
    }

    /* Обязательно очищаем тестовые БД, потому, что после неудачных тестов записи остаются в БД */
    @AfterAll
    public static void clearAnotherRepositories() {
        hbnEngineRepository.clearRepository();
        hbnOwnerRepository.clearRepository();
        userRepository.clearRepository();
    }

    /* Тестируем add() */
    @Test
    public void whenSaveThenGetSame() {
        Car carBeforeAdd1 = new Car(
                "Автомобиль с ГТД", 
                firstTestEngine, 
                firstTestOwner);
        Car carBeforeAdd2 = new Car(
                "Автомобиль бэтмента", 
                secondTestEngine, 
                thirdTestOwner, 
                Set.of(secondTestOwner, thirdTestOwner));
        List<Car> listBeforeAdd = hbnCarRepository.findAll();
        Car carAfterAdd1 = hbnCarRepository.add(carBeforeAdd1);
        Car carAfterAdd2 = hbnCarRepository.add(carBeforeAdd2);
        List<Car> listAfterAdd = hbnCarRepository.findAll();
        assertThat(carBeforeAdd1).isEqualTo(carAfterAdd1);
        assertThat(carBeforeAdd2).isEqualTo(carAfterAdd2);
        assertThat(listBeforeAdd.contains(carBeforeAdd1)).isFalse();
        assertThat(listBeforeAdd.contains(carBeforeAdd2)).isFalse();
        assertThat(listAfterAdd.contains(carBeforeAdd1)).isTrue();
        assertThat(listAfterAdd.contains(carBeforeAdd2)).isTrue();
        assertThat(listAfterAdd.size()).isGreaterThan(listBeforeAdd.size());
    }

    @Test
    public void whenSaveOneSameSeveralThenGetAll() {
        Car car = new Car(
                "Автомобиль бэтмента", 
                secondTestEngine, 
                thirdTestOwner, 
                Set.of(secondTestOwner, thirdTestOwner));
        List<Car> listBeforeAdd = hbnCarRepository.findAll();
        Car carAfterAdd1 = hbnCarRepository.add(car);
        Car carAfterAdd2 = hbnCarRepository.add(car);
        Car carAfterAdd3 = hbnCarRepository.add(car);
        List<Car> listAfterAdd = hbnCarRepository.findAll();
        assertThat(car).isEqualTo(carAfterAdd1).isEqualTo(carAfterAdd2).isEqualTo(carAfterAdd3);
        assertThat(listBeforeAdd.contains(car)).isFalse();
        assertThat(listBeforeAdd.size()).isEqualTo(0);
        assertThat(listAfterAdd.size()).isEqualTo(3);
        assertThat(listAfterAdd).contains(carAfterAdd1, carAfterAdd2, carAfterAdd3);
    }

    /* Тестируем replace() */
    @Test
    public void whenUpdateOneCarThenGetIt() {
        Car carBeforeReplace = new Car(
                "Автомобиль бэтмента",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car carAfterAdd1 = hbnCarRepository.add(carBeforeReplace);
        Car carAfterReplace1 = new Car(
                "Теперь это автомобиль не бэтмента",
                secondTestEngine,
                firstTestOwner,
                Set.of(secondTestOwner, thirdTestOwner, firstTestOwner));
        boolean success = hbnCarRepository.replace(carAfterAdd1.getId(), carAfterReplace1);
        List<Car> listAfterReplace = hbnCarRepository.findAll();
        assertThat(success).isTrue();
        assertThat(carAfterAdd1).isNotEqualTo(carAfterReplace1);
        assertThat(listAfterReplace.size()).isEqualTo(1);
        assertThat(listAfterReplace.contains(carAfterReplace1)).isTrue();
        assertThat(listAfterReplace.contains(carAfterAdd1)).isFalse();
    }

    /* Тестируем delete() */
    @Test
    public void whenSaveOneCarThenDeleteIt() {
        Car car = new Car(
                "Автомобиль бэтмента",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car carAfterAdd1 = hbnCarRepository.add(car);
        List<Car> listAfterAdd = hbnCarRepository.findAll();
        boolean success = hbnCarRepository.delete(carAfterAdd1.getId());
        List<Car> listAfterDelete = hbnCarRepository.findAll();
        assertThat(listAfterAdd.size()).isGreaterThan(listAfterDelete.size());
        assertThat(listAfterDelete.contains(carAfterAdd1)).isFalse();
        assertThat(success).isTrue();
    }

    @Test
    public void whenTryDeleteCarUsingWrongId() {
        Car car = new Car(
                "Автомобиль бэтмента",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car carAfterAdd1 = hbnCarRepository.add(car);
        List<Car> listAfterAdd = hbnCarRepository.findAll();
        boolean success = hbnCarRepository.delete(carAfterAdd1.getId() + 31);
        List<Car> listAfterDeleteFail = hbnCarRepository.findAll();
        assertThat(listAfterAdd.size()).isEqualTo(listAfterDeleteFail.size());
        assertThat(listAfterDeleteFail.contains(carAfterAdd1)).isTrue();
        assertThat(success).isFalse();
    }

    /* Тестируем findAllByName() */
    @Test
    public void whenSaveTwoDifferentCarsThenGetAll() {
        Car car1 = new Car(
                "Автомобиль бэтмента",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Автомобиль не бэтмента",
                secondTestEngine,
                firstTestOwner,
                Set.of(secondTestOwner, firstTestOwner));
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByName1 = hbnCarRepository.findAllByName(car1.getName());
        List<Car> carListFoundByName2 = hbnCarRepository.findAllByName(car2.getName());
        assertThat(carListFoundByName1.size()).isEqualTo(carListFoundByName2.size()).isEqualTo(1);
        assertThat(carListFoundByName1).isNotEqualTo(carListFoundByName2);
        assertThat(carListFoundByName1.get(0)).isEqualTo(car1);
        assertThat(carListFoundByName2.get(0)).isEqualTo(car2);
    }

    @Test
    public void whenSaveTwoSameByNameCarsThenGetAll() {
        Car car1 = new Car(
                "findAllByName",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "findAllByName",
                firstTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByName = hbnCarRepository.findAllByName(car1.getName());
        assertThat(carListFoundByName.size()).isEqualTo(2);
        assertThat(carListFoundByName.get(0)).isEqualTo(car1);
        assertThat(carListFoundByName.get(1)).isEqualTo(car2);
        assertThat(carListFoundByName.get(0)).isNotEqualTo(carListFoundByName.get(1));
    }

    @Test
    public void whenTryFindByNameThenGetNoting() {
        Car car1 = new Car(
                "Первый автомобиль",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Второй автомобиль",
                firstTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByName = hbnCarRepository.findAllByName("noting");
        assertThat(carListFoundByName.size()).isZero();
    }

    @Test
    public void whenTryFindByEmptyNameThenGetNoting() {
        Car car1 = new Car(
                "Первый автомобиль",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Второй автомобиль",
                firstTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByName = hbnCarRepository.findAllByName("");
        assertThat(carListFoundByName.size()).isZero();
    }

    /* Тестируем findById() */
    @Test
    public void whenFindByIdSuccess() {
        Car car1 = new Car(
                "Первый автомобиль",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Второй автомобиль",
                firstTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        Optional<Car> optionalCar1 = hbnCarRepository.findById(car1.getId());
        Optional<Car> optionalCar2 = hbnCarRepository.findById(car2.getId());
        assertThat(optionalCar1.isPresent()).isTrue();
        assertThat(optionalCar1.get()).isEqualTo(car1);
        assertThat(optionalCar2.isPresent()).isTrue();
        assertThat(optionalCar2.get()).isEqualTo(car2);
        assertThat(optionalCar1.get()).isNotEqualTo(optionalCar2.get());
    }

    @Test
    public void whenTryFindByWrongIdThenGetNoting() {
        Car car1 = new Car(
                "Первый автомобиль",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Второй автомобиль",
                firstTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        Optional<Car> optionalCar = hbnCarRepository.findById(car2.getId() + 1);
        assertThat(optionalCar.isPresent()).isFalse();
    }

    /* Тестируем findAllByEngine() */
    @Test
    public void whenSaveTwoDifferentCarsThenGetAllByEngine() {
        Car car1 = new Car(
                "Автомобиль бэтмента",
                firstTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Автомобиль не бэтмента",
                secondTestEngine,
                firstTestOwner,
                Set.of(secondTestOwner, firstTestOwner));
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByEngine1 = hbnCarRepository.findAllByEngine(firstTestEngine);
        List<Car> carListFoundByEngine2 = hbnCarRepository.findAllByEngine(secondTestEngine);
        assertThat(carListFoundByEngine1.size()).isEqualTo(carListFoundByEngine2.size()).isEqualTo(1);
        assertThat(carListFoundByEngine1).isNotEqualTo(carListFoundByEngine2);
        assertThat(carListFoundByEngine1.get(0)).isEqualTo(car1);
        assertThat(carListFoundByEngine2.get(0)).isEqualTo(car2);
    }

    @Test
    public void whenSaveTwoSameByEngineCarsThenGetAll() {
        Car car1 = new Car(
                "Автомобиль бэтмента",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Автомобиль не бэтмента",
                secondTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByEngine = hbnCarRepository.findAllByEngine(car1.getEngine());
        assertThat(carListFoundByEngine.size()).isEqualTo(2);
        assertThat(carListFoundByEngine.get(0)).isEqualTo(car1);
        assertThat(carListFoundByEngine.get(1)).isEqualTo(car2);
        assertThat(carListFoundByEngine.get(0)).isNotEqualTo(carListFoundByEngine.get(1));
    }

    @Test
    public void whenTryFindByEngineThenGetNoting() {
        Car car1 = new Car(
                "Первый автомобиль",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Второй автомобиль",
                firstTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByEngine = hbnCarRepository.findAllByEngine(thirdTestEngine);
        assertThat(carListFoundByEngine.size()).isZero();
    }

    @Test
    public void whenTryFindByNullEngineThenGetNoting() {
        Car car1 = new Car(
                "Первый автомобиль",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Второй автомобиль",
                firstTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByEngine = hbnCarRepository.findAllByEngine(null);
        assertThat(carListFoundByEngine.size()).isZero();
    }

    /* Тестируем findAllByOwner() */
    @Test
    public void whenSaveTwoDifferentCarsThenGetAllByOwner() {
        Car car1 = new Car(
                "Автомобиль бэтмента",
                firstTestEngine,
                firstTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Автомобиль не бэтмента",
                secondTestEngine,
                secondTestOwner,
                Set.of(secondTestOwner, firstTestOwner));
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByOwner1 = hbnCarRepository.findAllByOwner(firstTestOwner);
        List<Car> carListFoundByOwner2 = hbnCarRepository.findAllByOwner(secondTestOwner);
        assertThat(carListFoundByOwner1.size()).isEqualTo(carListFoundByOwner2.size()).isEqualTo(1);
        assertThat(carListFoundByOwner1).isNotEqualTo(carListFoundByOwner2);
        assertThat(carListFoundByOwner1.get(0)).isEqualTo(car1);
        assertThat(carListFoundByOwner2.get(0)).isEqualTo(car2);
    }

    @Test
    public void whenSaveTwoSameByOwnerCarsThenGetAll() {
        Car car1 = new Car(
                "Автомобиль бэтмента",
                firstTestEngine,
                firstTestOwner,
                Set.of(secondTestOwner, firstTestOwner));
        Car car2 = new Car(
                "Автомобиль не бэтмента",
                secondTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByOwner = hbnCarRepository.findAllByOwner(car1.getOwner());
        assertThat(carListFoundByOwner.size()).isEqualTo(2);
        assertThat(carListFoundByOwner.get(0)).isEqualTo(car1);
        assertThat(carListFoundByOwner.get(1)).isEqualTo(car2);
        assertThat(carListFoundByOwner.get(0)).isNotEqualTo(carListFoundByOwner.get(1));
    }

    @Test
    public void whenTryFindByOwnerThenGetNoting() {
        Car car1 = new Car(
                "Первый автомобиль",
                secondTestEngine,
                secondTestOwner,
                Set.of(secondTestOwner));
        Car car2 = new Car(
                "Второй автомобиль",
                firstTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByOwner = hbnCarRepository.findAllByOwner(thirdTestOwner);
        assertThat(carListFoundByOwner.size()).isZero();
    }

    @Test
    public void whenTryFindByNullOwnerThenGetNoting() {
        Car car1 = new Car(
                "Первый автомобиль",
                secondTestEngine,
                thirdTestOwner,
                Set.of(secondTestOwner, thirdTestOwner));
        Car car2 = new Car(
                "Второй автомобиль",
                firstTestEngine,
                firstTestOwner);
        hbnCarRepository.add(car1);
        hbnCarRepository.add(car2);
        List<Car> carListFoundByOwner = hbnCarRepository.findAllByOwner(null);
        assertThat(carListFoundByOwner.size()).isZero();
    }

}