package com.halykmarket.merchant.telegabot.repository;

import com.halykmarket.merchant.telegabot.model.standart.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<User, Integer> {
    int countUserByChatId(long chatId);

    boolean existsByChatId(long chatId);

    Optional<User> findFirstByChatId(long chatId);

}
