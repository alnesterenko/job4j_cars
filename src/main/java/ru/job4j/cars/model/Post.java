package ru.job4j.cars.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "prices", "photos", "participates"})
@Entity
@Table(name = "auto_post")
public class Post {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-EEEE-yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;
    private LocalDateTime created = LocalDateTime.now(ZoneId.of("UTC")).truncatedTo(ChronoUnit.SECONDS);

    @ManyToOne
    @JoinColumn(name = "auto_user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "car_id", foreignKey = @ForeignKey(name = "CAR_ID_FK"))
    private Car car;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private List<PriceHistory> prices = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Photo> photos = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "participates",
            joinColumns = { @JoinColumn(name = "post_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> participates = new HashSet<>();

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", description=" + description
                + ", created=" + created.format(FORMATTER)
                + ", user=" + user
                + ", car=" + car
                + ", prices=" + prices
                + ", photos=" + photos
                + ", participates=" + participates
                + '}';
    }

    public Post(String description, User user, Car car) {
        this.description = description;
        this.user = user;
        this.car = car;
    }

    public Post(String description, User user, Car car, List<PriceHistory> prices) {
        this.description = description;
        this.user = user;
        this.car = car;
        this.prices = prices;
    }

    public Post(String description, User user, Car car, List<PriceHistory> prices, Set<Photo> photos) {
        this.description = description;
        this.user = user;
        this.car = car;
        this.prices = prices;
        this.photos = photos != null ? new HashSet<>(photos) : new HashSet<>();
        for (Photo photo : this.photos) {
            photo.setPost(this);
        }
    }

    public Post(String description, User user, Car car, List<PriceHistory> prices, Set<Photo> photos, Set<User> participates) {
        this.description = description;
        this.user = user;
        this.car = car;
        this.prices = prices;
        this.photos = photos != null ? new HashSet<>(photos) : new HashSet<>();
        for (Photo photo : this.photos) {
            photo.setPost(this);
        }
        this.participates = participates;
    }

    public Post(String description, User user, Car car, Set<Photo> photos) {
        this.description = description;
        this.user = user;
        this.car = car;
        this.photos = photos != null ? new HashSet<>(photos) : new HashSet<>();
        for (Photo photo : this.photos) {
            photo.setPost(this);
        }
    }
}
