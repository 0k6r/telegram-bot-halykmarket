package com.halykmarket.merchant.telegabot.model.standart;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class LanguageUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Long chatId;
    private Integer languageId;

    public LanguageUser setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public LanguageUser setLanguageId(int languageId) {
        this.languageId = languageId;
        return this;
    }
}
