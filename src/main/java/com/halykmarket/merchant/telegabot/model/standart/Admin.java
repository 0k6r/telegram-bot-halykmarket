package com.halykmarket.merchant.telegabot.model.standart;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long chatId;

    @Column(length = 500)
    private String comment;

    public Admin setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public Admin setComment(String comment) {
        this.comment = comment;
        return this;
    }
}
