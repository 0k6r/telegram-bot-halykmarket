package com.halykmarket.merchant.telegabot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.halykmarket.merchant.telegabot.model.standart.LanguageUser;

@Repository
public interface LanguageUserRepo extends CrudRepository<LanguageUser, Integer> {
    LanguageUser getByChatId(long chatId);
}
