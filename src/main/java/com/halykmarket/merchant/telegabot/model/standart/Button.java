package com.halykmarket.merchant.telegabot.model.standart;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Button {

    @Id
    @Column(unique = false)
    private int     id;

    @Column(length = 4096)
    private String  name;

    private Integer commandId;
    private String  url;
    private boolean requestContact;
    private Integer messageId;
    private int     langId;
}
