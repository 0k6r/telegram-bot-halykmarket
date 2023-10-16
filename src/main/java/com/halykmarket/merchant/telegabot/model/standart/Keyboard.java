package com.halykmarket.merchant.telegabot.model.standart;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Keyboard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(length = 4096)
    private String buttonIds;

    private boolean inline;

    @Column(length = 4096)
    private String comment;
}
