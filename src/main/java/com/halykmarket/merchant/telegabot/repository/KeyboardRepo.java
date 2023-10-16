package com.halykmarket.merchant.telegabot.repository;

import com.halykmarket.merchant.telegabot.model.standart.Keyboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyboardRepo extends JpaRepository<Keyboard, Integer> {

}
