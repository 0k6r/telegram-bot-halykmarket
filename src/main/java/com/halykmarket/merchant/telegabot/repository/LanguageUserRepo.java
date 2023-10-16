package com.halykmarket.merchant.telegabot.repository;

import com.halykmarket.merchant.telegabot.model.standart.LanguageUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageUserRepo extends JpaRepository<LanguageUser, Integer> {
    Optional<LanguageUser> findByChatId(long chatId);
}
