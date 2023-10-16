package com.halykmarket.merchant.telegabot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.halykmarket.merchant.telegabot.model.standart.Keyboard;

@Repository
public interface KeyboardRepo extends CrudRepository<Keyboard, Integer> {


    @Query("select k.inline from Keyboard k where k.id = ?1")
    boolean isInline(int keyboardId);

    @Query("select k.buttonIds from Keyboard  k where k.id = ?1")
    String getButtonString(int id);
}
