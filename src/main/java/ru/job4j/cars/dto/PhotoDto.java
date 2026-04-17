package ru.job4j.cars.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data /* Не создаёт конструктор со всеми полями */
@AllArgsConstructor
@EqualsAndHashCode
public class PhotoDto {

    private String name;

    private byte[] content; /* Тут кроется различие. Доменная модель хранит путь, а не содержимое */
}
