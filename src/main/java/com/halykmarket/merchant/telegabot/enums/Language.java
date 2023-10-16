package com.halykmarket.merchant.telegabot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum Language {

    RU(1),
    KZ(2),
    EN(3);

    private int id;

    public static Language getById(int id) {
        for (Language language : values()) {
            if (language.id == (id)) return language;
        }
        return KZ;
    }

}
