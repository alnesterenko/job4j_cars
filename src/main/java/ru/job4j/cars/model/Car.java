package ru.job4j.cars.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "car")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /* Если это не марка машины, то что это? Имя собственное? "Чертопхайка", "Колымага", "Чёртов пылесос" ? ))) */
    /* Пока оставляю так. Если в дальнейшем что-то прояснится, то заменю на model, добавлю связь manyToOne, добавлю отделную таблицу и репозиторий */
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id", foreignKey = @ForeignKey(name = "ENGINE_ID_FK"))
    private Engine engine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "OWNER_ID_FK"))
    private Owner owner;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "history_owners",
            joinColumns = {@JoinColumn(name = "car_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "owner_id", nullable = false, updatable = false)})
    private Set<Owner> owners = new HashSet<>();

    public Car(String name, Engine engine, Owner owner) {
        this.name = name;
        this.engine = engine;
        this.owner = owner;
    }

    public Car(String name, Engine engine, Owner owner, Set<Owner> owners) {
        this.name = name;
        this.engine = engine;
        this.owner = owner;
        this.owners = owners;
    }
}