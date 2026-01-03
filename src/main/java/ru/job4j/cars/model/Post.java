package ru.job4j.cars.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "auto_post")
public class Post {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-EEEE-yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;
    private LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    @ManyToOne
    @JoinColumn(name = "auto_user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private List<PriceHistory> prices = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "participates",
            joinColumns = { @JoinColumn(name = "post_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private List<User> participates = new ArrayList<>();

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", description='" + description + '\''
                + ", created=" + created.format(FORMATTER)
                + ", user=" + user
                + ", prices=" + prices
                + '}';
    }
}
