package com.halykmarket.merchant.telegabot.model.standart;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "come_times")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ComeTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @OneToOne(fetch = FetchType.EAGER)
    User user;

    /**
     * время создания
     */
    @Column
    LocalDate createdDate = LocalDate.now();

    /**
     * время обновления
     */
    @Column
    LocalTime createdTime;
}
