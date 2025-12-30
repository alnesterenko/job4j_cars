package ru.job4j.cars.model;

import javax.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private BigInteger before;
    private BigInteger after;
    private LocalDateTime created;
}
