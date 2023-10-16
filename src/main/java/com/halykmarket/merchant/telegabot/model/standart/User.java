package com.halykmarket.merchant.telegabot.model.standart;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long chatId;
    private String phone;
    private String fullName;
    private String position;
    private String workSchedule;
    @Column(length = 500)
    private String username;

}