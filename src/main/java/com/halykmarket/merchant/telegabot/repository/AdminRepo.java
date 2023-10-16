package com.halykmarket.merchant.telegabot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.halykmarket.merchant.telegabot.model.standart.Admin;

import java.util.List;

@Repository
public interface AdminRepo extends CrudRepository<Admin, Integer> {
    int countByChatId(long chatId);
    List<Admin> findAll();
}
