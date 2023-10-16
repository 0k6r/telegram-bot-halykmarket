package com.halykmarket.merchant.telegabot.model.standart;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class LanguageUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer     id;

    private Long        chatId;
    private Integer     languageId;

    public LanguageUser setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public LanguageUser setLanguageId(int languageId) {
        this.languageId = languageId;
        return this;
    }
}
