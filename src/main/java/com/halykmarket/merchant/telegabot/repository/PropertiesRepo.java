package com.halykmarket.merchant.telegabot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.halykmarket.merchant.telegabot.model.standart.Properties;

@Repository
public interface PropertiesRepo extends CrudRepository<Properties, Integer> {
Properties findFirstById(int id);
}
