package ru.job4j.cars.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.configuration.HibernateConfiguration;
import ru.job4j.cars.model.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HbnPostRepositoryTest {

    private static PostRepository hbnPostRepository;

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

    private static Car firstTestCar = new Car("Автомобиль с ГТД", firstTestEngine);
    private static Car secondTestCar = new Car("Автомобиль бэтмента", secondTestEngine, Set.of(secondTestOwner, thirdTestOwner));
    private static Car thirdTestCar = new Car("Эксперементальное авто", thirdTestEngine);

    @BeforeAll
    public static void initRepository() {
        hbnPostRepository = new HbnPostRepository(crudRepository);
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

        hbnCarRepository.clearRepository();
        hbnCarRepository.add(firstTestCar);
        hbnCarRepository.add(secondTestCar);
        hbnCarRepository.add(thirdTestCar);
    }

    @AfterEach
    public void clearPosts() {
        hbnPostRepository.clearRepository();
    }

    /* Обязательно очищаем тестовые БД, потому, что после неудачных тестов записи остаются в БД */
    @AfterAll
    public static void clearAnotherRepositories() {
        hbnCarRepository.clearRepository();
        hbnEngineRepository.clearRepository();
        hbnOwnerRepository.clearRepository();
        userRepository.clearRepository();
    }

    /* Тестируем add() */
    @Test
    public void whenSaveThenGetSame() {
        Post postBeforeAdd1 = new Post(
                "Первое тестовое объявление",
                firstTestUser,
                firstTestCar);
        Post postBeforeAdd2 = new Post(
                "Второе тестовое объявление",
                secondTestUser,
                secondTestCar);
        List<Post> listBeforeAdd = hbnPostRepository.findAll();
        Post postAfterAdd1 = hbnPostRepository.add(postBeforeAdd1);
        Post postAfterAdd2 = hbnPostRepository.add(postBeforeAdd2);
        List<Post> listAfterAdd = hbnPostRepository.findAll();
        assertThat(postBeforeAdd1).isEqualTo(postAfterAdd1);
        assertThat(postBeforeAdd2).isEqualTo(postAfterAdd2);
        assertThat(listBeforeAdd.contains(postBeforeAdd1)).isFalse();
        assertThat(listBeforeAdd.contains(postBeforeAdd2)).isFalse();
        assertThat(listAfterAdd.contains(postBeforeAdd1)).isTrue();
        assertThat(listAfterAdd.contains(postBeforeAdd2)).isTrue();
        assertThat(listAfterAdd.size()).isGreaterThan(listBeforeAdd.size());
    }

    @Test
    public void whenSaveTwiceThenGetException() {
        Post post = new Post(
                "Тестовое объявление, которое должно быть добавлено несколько раз",
                firstTestUser,
                firstTestCar,
                Set.of(new Photo("firstTestSavePhoto")));
        List<Post> listBeforeAdd = hbnPostRepository.findAll();
        Post postAfterAdd1 = hbnPostRepository.add(post);
        Exception exception = assertThrows(ConstraintViolationException.class, () -> {
            hbnPostRepository.add(post);
        });
        List<Post> listAfterAdd = hbnPostRepository.findAll();
        assertThat(post).isEqualTo(postAfterAdd1);
        assertThat(listBeforeAdd.contains(post)).isFalse();
        assertThat(listAfterAdd.size()).isEqualTo(1);
        assertThat(listAfterAdd).contains(postAfterAdd1);
        assertThat("could not execute statement").isEqualTo(exception.getMessage());
    }

    /* Тестируем replace() */
    @Test
    public void whenUpdateOnePostThenGetIt() {
        Post postBeforeReplace = new Post(
                "Объявление до замены",
                firstTestUser,
                firstTestCar,
                Set.of(new Photo("firstTestReplacePhoto")));
        Post postAfterAdd1 = hbnPostRepository.add(postBeforeReplace);
        Post postAfterReplace1 = new Post(
                "Объявление после замены",
                secondTestUser,
                secondTestCar,
                Set.of(new Photo("secondTestReplacePhoto"), new Photo("thirdTestReplacePhoto")));
        boolean success = hbnPostRepository.replace(postAfterAdd1.getId(), postAfterReplace1);
        List<Post> listAfterReplace = hbnPostRepository.findAll();
        assertThat(success).isTrue();
        assertThat(postAfterAdd1).isNotEqualTo(postAfterReplace1);
        assertThat(listAfterReplace.size()).isEqualTo(1);
        assertThat(listAfterReplace.contains(postAfterReplace1)).isTrue();
        assertThat(listAfterReplace.contains(postAfterAdd1)).isFalse();
    }

    /* Тестируем delete() */
    @Test
    public void whenSaveOnePostThenDeleteIt() {
        Post post = new Post(
                "Объявление, которое будет удалено",
                secondTestUser,
                thirdTestCar,
                Set.of(new Photo("firstCorrectDeletePhoto"), new Photo("secondCorrectDeletePhoto")));
        Post postAfterAdd1 = hbnPostRepository.add(post);
        List<Post> listAfterAdd = hbnPostRepository.findAll();
        boolean success = hbnPostRepository.delete(postAfterAdd1.getId());
        List<Post> listAfterDelete = hbnPostRepository.findAll();
        assertThat(listAfterAdd.size()).isGreaterThan(listAfterDelete.size());
        assertThat(listAfterDelete.contains(postAfterAdd1)).isFalse();
        assertThat(success).isTrue();
    }

    @Test
    public void whenTryDeletePostUsingWrongId() {
        Post post = new Post(
                "Объявление, которое не будет удалено",
                secondTestUser,
                thirdTestCar,
                Set.of(new Photo("firstWrongDeletePhoto"), new Photo("secondWrongDeletePhoto")));
        Post postAfterAdd1 = hbnPostRepository.add(post);
        List<Post> listAfterAdd = hbnPostRepository.findAll();
        boolean success = hbnPostRepository.delete(postAfterAdd1.getId() + 31);
        List<Post> listAfterDeleteFail = hbnPostRepository.findAll();
        assertThat(listAfterAdd.size()).isEqualTo(listAfterDeleteFail.size());
        assertThat(listAfterDeleteFail.contains(postAfterAdd1)).isTrue();
        assertThat(success).isFalse();
    }

    /* Тестируем findById() */
    @Test
    public void whenSavePostsThenGetByIdAll() {
        Post post1 = new Post(
                "Первое объявление",
                firstTestUser,
                firstTestCar,
                Set.of(new Photo("firstCorrectDeletePhoto"), new Photo("secondCorrectDeletePhoto")));
        Post post2 = new Post(
                "Второе объявление",
                secondTestUser,
                secondTestCar);
        hbnPostRepository.add(post1);
        hbnPostRepository.add(post2);
        var optionalPost1 = hbnPostRepository.findById(post1.getId());
        var optionalPost2 = hbnPostRepository.findById(post2.getId());
        assertThat(optionalPost1.isPresent()).isTrue();
        assertThat(optionalPost1.orElse(null)).isEqualTo(post1);
        assertThat(optionalPost2.isPresent()).isTrue();
        assertThat(optionalPost2.orElse(null)).isEqualTo(post2);
    }

    @Test
    public void whenTryGetByIdUsingWrongId() {
        Post post = new Post(
                "Объявление, которое не будет найдено",
                secondTestUser,
                thirdTestCar,
                Set.of(new Photo("firstWrongDeletePhoto"), new Photo("secondWrongDeletePhoto")));
        Post postAfterAdd1 = hbnPostRepository.add(post);
        var optionalPost = hbnPostRepository.findById(postAfterAdd1.getId() + 31);
        assertThat(optionalPost).isEmpty();
        assertThat(optionalPost.orElse(null)).isNull();
    }

    /* Тестируем findAllByDescription() */
    @Test
    public void whenSavePostsThenGetByDescription() {
        Post post1 = new Post(
                "Объявление 1",
                firstTestUser,
                firstTestCar);
        Post post2 = new Post(
                "Объявление 12",
                secondTestUser,
                secondTestCar);
        Post post3 = new Post(
                "Объявление 123",
                thirdTestUser,
                thirdTestCar);
        hbnPostRepository.add(post1);
        hbnPostRepository.add(post2);
        hbnPostRepository.add(post3);
        var exactMatchList = hbnPostRepository.findAllByDescription("Объявление 1");
        var containedInList = hbnPostRepository.findAllByDescription("Объявление 1", true);
        var findAllList = hbnPostRepository.findAll();
        assertThat(exactMatchList.size()).isEqualTo(1);
        assertThat(exactMatchList).contains(post1);
        assertThat(containedInList.size()).isEqualTo(3);
        assertThat(containedInList).contains(post1, post2, post3);
        assertThat(findAllList.size()).isEqualTo(containedInList.size());
        assertThat(exactMatchList).isNotEqualTo(containedInList);
    }

    @Test
    public void whenTryGetByWrongDescription() {
        Post post = new Post(
                "Объявление, которое не будет найдено",
                secondTestUser,
                thirdTestCar,
                Set.of(new Photo("firstWrongDeletePhoto"), new Photo("secondWrongDeletePhoto")));
        hbnPostRepository.add(post);
        var findAllList = hbnPostRepository.findAll();
        var exactMatchList = hbnPostRepository.findAllByDescription("черепаха");
        var containedInList = hbnPostRepository.findAllByDescription("зёбра", true);
        assertThat(exactMatchList.size()).isZero();
        assertThat(containedInList.size()).isZero();
        assertThat(findAllList.size()).isEqualTo(1);
    }

    /* Тестируем findByCar() */
    @Test
    public void whenSavePostsThenGetByCar() {
        Post post1 = new Post(
                "Объявление 1",
                firstTestUser,
                firstTestCar);
        Post post2 = new Post(
                "Объявление 12",
                secondTestUser,
                secondTestCar);
        Post post3 = new Post(
                "Объявление 123",
                thirdTestUser,
                thirdTestCar);
        hbnPostRepository.add(post1);
        hbnPostRepository.add(post2);
        hbnPostRepository.add(post3);
        var firstOptional = hbnPostRepository.findByCar(firstTestCar);
        var secondOptional = hbnPostRepository.findByCar(secondTestCar);
        var thirdOptional = hbnPostRepository.findByCar(thirdTestCar);
        assertThat(firstOptional.isPresent()).isTrue();
        assertThat(firstOptional.orElse(null)).isEqualTo(post1);
        assertThat(secondOptional.isPresent()).isTrue();
        assertThat(secondOptional.orElse(null)).isEqualTo(post2);
        assertThat(thirdOptional.isPresent()).isTrue();
        assertThat(thirdOptional.orElse(null)).isEqualTo(post3);
    }

    @Test
    public void whenTryGetByWrongCar() {
        Post post1 = new Post(
                "Объявление 1",
                firstTestUser,
                firstTestCar);
        Post post2 = new Post(
                "Объявление 12",
                secondTestUser,
                secondTestCar);
        hbnPostRepository.add(post1);
        hbnPostRepository.add(post2);
        var postOptional = hbnPostRepository.findByCar(thirdTestCar);
        var findAllList = hbnPostRepository.findAll();
        assertThat(findAllList.size()).isGreaterThan(0);
        assertThat(postOptional.isPresent()).isFalse();
        assertThat(postOptional.orElse(null)).isEqualTo(null);
    }

    /* Тестируем findAllByUser() */
    @Test
    public void whenSavePostsThenGetByUser() {
        Post post1 = new Post(
                "Объявление 1",
                firstTestUser,
                firstTestCar);
        Post post2 = new Post(
                "Объявление 12",
                secondTestUser,
                secondTestCar);
        Post post3 = new Post(
                "Объявление 123",
                secondTestUser,
                thirdTestCar);
        hbnPostRepository.add(post1);
        hbnPostRepository.add(post2);
        hbnPostRepository.add(post3);
        var firstUserList = hbnPostRepository.findAllByUser(firstTestUser);
        var secondUserList = hbnPostRepository.findAllByUser(secondTestUser);
        assertThat(firstUserList.size()).isEqualTo(1);
        assertThat(secondUserList.size()).isEqualTo(2);
    }

    @Test
    public void whenTryGetByWrongUser() {
        Post post1 = new Post(
                "Объявление 1",
                firstTestUser,
                firstTestCar);
        Post post2 = new Post(
                "Объявление 12",
                secondTestUser,
                secondTestCar);
        hbnPostRepository.add(post1);
        hbnPostRepository.add(post2);
        var thirdUserList = hbnPostRepository.findAllByUser(thirdTestUser);
        var findAllList = hbnPostRepository.findAll();
        assertThat(findAllList.size()).isGreaterThan(0);
        assertThat(thirdUserList.size()).isZero();
    }

    /* Тестируем showAllForLastDay() */
    @Test
    public void whenSavePostsThenGetAllForLastDay() {
        Post post1 = new Post(
                "Объявление 1",
                firstTestUser,
                firstTestCar);
        Post post2 = new Post(
                "Объявление 12",
                secondTestUser,
                secondTestCar);
        Post post3 = new Post(
                "Объявление 123",
                secondTestUser,
                thirdTestCar);
        hbnPostRepository.add(post1);
        hbnPostRepository.add(post2);
        hbnPostRepository.add(post3);
        post3.setCreated(LocalDateTime.now(ZoneId.of("UTC")).minusYears(1));
        hbnPostRepository.replace(post3.getId(), post3);
        var lastDayPostsList = hbnPostRepository.showAllForLastDay();
        assertThat(lastDayPostsList.size()).isEqualTo(2);
    }

    @Test
    public void whenTryGetByAllForLastDayThenFail() {
        Post post3 = new Post(
                "Объявление 123",
                secondTestUser,
                thirdTestCar);
        hbnPostRepository.add(post3);
        post3.setCreated(LocalDateTime.now(ZoneId.of("UTC")).minusYears(1));
        hbnPostRepository.replace(post3.getId(), post3);
        var lastDayPostsList = hbnPostRepository.showAllForLastDay();
        var findAllList = hbnPostRepository.findAll();
        assertThat(lastDayPostsList.size()).isEqualTo(0);
        assertThat(findAllList.size()).isGreaterThan(0);
    }

    /* Тестируем showAllWhereArePhoto() */
    @Test
    public void whenSavePostsThenGetAllWhereArePhoto() {
        Post post1 = new Post(
                "Объявление 1",
                firstTestUser,
                firstTestCar,
                Set.of(new Photo("firstPhoto"), new Photo("secondPhoto")));
        Post post2 = new Post(
                "Объявление 12",
                secondTestUser,
                secondTestCar,
                Set.of(new Photo("thirdPhoto")));
        Post post3 = new Post(
                "Объявление 123",
                secondTestUser,
                thirdTestCar);
        hbnPostRepository.add(post1);
        hbnPostRepository.add(post2);
        hbnPostRepository.add(post3);
        var findAllList = hbnPostRepository.findAll();
        var whereArePhotoPostList = hbnPostRepository.showAllWhereArePhoto();
        assertThat(whereArePhotoPostList.size()).isEqualTo(2);
        assertThat(findAllList.size()).isGreaterThan(whereArePhotoPostList.size());
    }

    @Test
    public void whenTryGetAllWhereArePhotoThenFail() {
        Post post3 = new Post(
                "Объявление 123",
                secondTestUser,
                thirdTestCar);
        hbnPostRepository.add(post3);
        var findAllList = hbnPostRepository.findAll();
        var whereArePhotoPostList = hbnPostRepository.showAllWhereArePhoto();
        assertThat(whereArePhotoPostList.size()).isEqualTo(0);
        assertThat(findAllList.size()).isGreaterThan(whereArePhotoPostList.size());
    }

    @Test
    public void whenSavePostThenReplacePhoto() {
        Post post3 = new Post(
                "Объявление 123",
                secondTestUser,
                thirdTestCar,
                Set.of(new Photo("firstPhoto"), new Photo("secondPhoto")));
        hbnPostRepository.add(post3);
        var findAllList1 = hbnPostRepository.findAll();
        var whereArePhotoPostList1 = hbnPostRepository.showAllWhereArePhoto();
        post3.setPhotos(Set.of());
        hbnPostRepository.replace(post3.getId(), post3);
        var findAllList2 = hbnPostRepository.findAll();
        var whereArePhotoPostList2 = hbnPostRepository.showAllWhereArePhoto();
        assertThat(findAllList1).isEqualTo(findAllList2);
        assertThat(whereArePhotoPostList1.size()).isEqualTo(1);
        assertThat(whereArePhotoPostList2.size()).isEqualTo(0);
    }

    /* Тестируем showAllByCarName() */
    @Test
    public void whenSavePostsThenGetByCarName() {
        Post post1 = new Post(
                "Объявление 1",
                firstTestUser,
                firstTestCar);
        Post post2 = new Post(
                "Объявление 2",
                secondTestUser,
                secondTestCar);
        Post post3 = new Post(
                "Объявление 3",
                thirdTestUser,
                thirdTestCar);
        hbnPostRepository.add(post1);
        hbnPostRepository.add(post2);
        hbnPostRepository.add(post3);
        var exactMatchList = hbnPostRepository.showAllByCarName("Автомобиль");
        var containedInList = hbnPostRepository.showAllByCarName("Автомобиль", true);
        var findAllList = hbnPostRepository.findAll();
        assertThat(exactMatchList.size()).isEqualTo(0);
        assertThat(containedInList.size()).isEqualTo(2);
        assertThat(containedInList).contains(post1, post2);
        assertThat(findAllList.size()).isGreaterThan(containedInList.size());
    }

    @Test
    public void whenTryGetByWrongCarName() {
        Post post = new Post(
                "Объявление, которое не будет найдено",
                secondTestUser,
                thirdTestCar,
                Set.of(new Photo("firstWrongDeletePhoto"), new Photo("secondWrongDeletePhoto")));
        hbnPostRepository.add(post);
        var findAllList = hbnPostRepository.findAll();
        var exactMatchList = hbnPostRepository.showAllByCarName("черепаха");
        var containedInList = hbnPostRepository.showAllByCarName("зёбра", true);
        assertThat(exactMatchList.size()).isZero();
        assertThat(containedInList.size()).isZero();
        assertThat(findAllList.size()).isEqualTo(1);
    }
}