package com.halykmarket.merchant.telegabot.model.standart;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "out_times")
@NoArgsConstructor
public class OutTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    /**
     * время создания
     */
    @Column
    private LocalDate createdDate = LocalDate.now();

    /**
     * время обновления
     */
    @Column
    private LocalTime createdTime = LocalTime.now();
}
