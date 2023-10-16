package com.halykmarket.merchant.telegabot.model.standart;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Properties {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 4096)
    private String name;

    @Column(length = 4096)
    private String value;
}
