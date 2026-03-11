package ru.job4j.cars.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.configuration.HibernateConfiguration;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HbnOwnerRepositoryTest {

    private static HbnOwnerRepository hbnOwnerRepository;

    private static UserRepository userRepository;

    private static HibernateConfiguration hibernateConfiguration = new HibernateConfiguration();

    private static CrudRepository crudRepository = new CrudRepository(hibernateConfiguration.sf());

    private static User firstTestUser = new User("111", "111");
    private static User secondTestUser = new User("222", "222");

    private static Owner firstTestOwner = new Owner("firstOwner", firstTestUser);
    private static Owner secondTestOwner = new Owner("secondOwner", secondTestUser);

    @BeforeAll
    public static void initRepository() {
        hbnOwnerRepository = new HbnOwnerRepository(crudRepository);
        userRepository = new UserRepository(hibernateConfiguration.sf());

        userRepository.clearRepository();
        userRepository.create(firstTestUser);
        userRepository.create(secondTestUser);
    }

    @AfterEach
    public void clearOwners() {
        hbnOwnerRepository.clearRepository();
    }

    @AfterAll
    public static void clearAnotherRepositories() {
        userRepository.clearRepository();
    }

    /* Тестируем add() */
    @Test
    public void whenSaveThenGetSame() {
        List<Owner> listBeforeAdd = hbnOwnerRepository.findAll();
        Owner ownerAfterAdd = hbnOwnerRepository.add(firstTestOwner);
        List<Owner> listAfterAdd = hbnOwnerRepository.findAll();
        assertThat(listBeforeAdd.contains(firstTestOwner)).isFalse();
        assertThat(listAfterAdd.contains(firstTestOwner)).isTrue();
        assertThat(listAfterAdd.size()).isGreaterThan(listBeforeAdd.size());
        assertThat(listAfterAdd.get(0)).isEqualTo(firstTestOwner);
        assertThat(listAfterAdd.get(0)).isNotEqualTo(secondTestOwner);
        assertThat(ownerAfterAdd).isEqualTo(firstTestOwner);
    }

    @Test
    public void whenSaveTwiceThenGetException() {
        List<Owner> listBeforeAdd = hbnOwnerRepository.findAll();
        hbnOwnerRepository.add(firstTestOwner);
        hbnOwnerRepository.add(secondTestOwner);
        Exception exception = assertThrows(ConstraintViolationException.class, () -> {
            hbnOwnerRepository.add(firstTestOwner);
        });
        List<Owner> listAfterAdd = hbnOwnerRepository.findAll();
        assertThat("could not execute statement").isEqualTo(exception.getMessage());
        assertThat(listBeforeAdd.size()).isZero();
        assertThat(listAfterAdd.size()).isEqualTo(2);
        assertThat(listAfterAdd).isEqualTo(List.of(firstTestOwner, secondTestOwner));
    }

    /* Тестируем findByName() */
    @Test
    public void whenFindByNameSuccess() {
        var ownerAfterAdd1 = hbnOwnerRepository.add(firstTestOwner);
        var ownerAfterAdd2 = hbnOwnerRepository.add(secondTestOwner);
        var foundOwner1Optional = hbnOwnerRepository.findByName(firstTestOwner.getName());
        var foundOwner2Optional = hbnOwnerRepository.findByName(secondTestOwner.getName());
        assertThat(ownerAfterAdd1).isEqualTo(foundOwner1Optional.orElse(null));
        assertThat(ownerAfterAdd2).isEqualTo(foundOwner2Optional.orElse(null));
    }

    @Test
    public void whenTryFindByWrongNameThenGetFail() {
        hbnOwnerRepository.add(firstTestOwner);
        hbnOwnerRepository.add(secondTestOwner);
        var foundOwner1Optional = hbnOwnerRepository.findByName(firstTestOwner.getName() + "31");
        var foundOwner2Optional = hbnOwnerRepository.findByName(secondTestOwner.getName() + 64);
        assertThat(foundOwner1Optional).isEmpty();
        assertThat(foundOwner2Optional).isEmpty();
    }

    /* Тестируем findByUserId() */
    @Test
    public void whenFindByUserIdSuccess() {
        var ownerAfterAdd1 = hbnOwnerRepository.add(firstTestOwner);
        var ownerAfterAdd2 = hbnOwnerRepository.add(secondTestOwner);
        var foundOwner1Optional = hbnOwnerRepository.findByUserId(firstTestUser.getId());
        var foundOwner2Optional = hbnOwnerRepository.findByUserId(secondTestUser.getId());
        assertThat(ownerAfterAdd1).isEqualTo(foundOwner1Optional.orElse(null));
        assertThat(ownerAfterAdd2).isEqualTo(foundOwner2Optional.orElse(null));
    }

    @Test
    public void whenTryFindByWrongUserIdThenGetFail() {
        hbnOwnerRepository.add(firstTestOwner);
        var ownersList = hbnOwnerRepository.findAll();
        var foundOwnerOptional = hbnOwnerRepository.findByUserId(secondTestUser.getId());
        assertThat(ownersList.size()).isEqualTo(1);
        assertThat(foundOwnerOptional).isEmpty();
    }

    /* Тестируем findById() */
    @Test
    public void whenFindByIdSuccess() {
        var ownerAfterAdd1 = hbnOwnerRepository.add(firstTestOwner);
        var ownerAfterAdd2 = hbnOwnerRepository.add(secondTestOwner);
        var foundOwner1Optional = hbnOwnerRepository.findById(firstTestOwner.getId());
        var foundOwner2Optional = hbnOwnerRepository.findById(secondTestOwner.getId());
        assertThat(ownerAfterAdd1).isEqualTo(foundOwner1Optional.orElse(null));
        assertThat(ownerAfterAdd2).isEqualTo(foundOwner2Optional.orElse(null));
    }

    @Test
    public void whenTryFindByWrongIdThenGetFail() {
        hbnOwnerRepository.add(firstTestOwner);
        var ownersList = hbnOwnerRepository.findAll();
        var foundOwnerOptional = hbnOwnerRepository.findById(firstTestOwner.getId() + 31);
        assertThat(ownersList.size()).isEqualTo(1);
        assertThat(foundOwnerOptional).isEmpty();
    }

    /* Тестируем delete() */
    @Test
    public void whenTryDeleteByWrongIdThenGetFail() {
        hbnOwnerRepository.add(firstTestOwner);
        var ownersListBeforeDelete = hbnOwnerRepository.findAll();
        var result = hbnOwnerRepository.delete(firstTestOwner.getId() + 31);
        var ownerListAfterDelete = hbnOwnerRepository.findAll();
        assertThat(result).isFalse();
        assertThat(ownersListBeforeDelete).isEqualTo(ownerListAfterDelete);
    }
}