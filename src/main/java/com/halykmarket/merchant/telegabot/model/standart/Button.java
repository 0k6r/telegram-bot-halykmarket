package com.halykmarket.merchant.telegabot.model.standart;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Button {

    @Id
    private int id;

    @Column(length = 4096)
    private String name;
    private Integer commandId;
    private String url;
    private boolean requestContact;
    private Integer messageId;
    private int langId;
}


