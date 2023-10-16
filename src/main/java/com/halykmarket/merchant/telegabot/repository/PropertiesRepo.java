package com.halykmarket.merchant.telegabot.repository;

import com.halykmarket.merchant.telegabot.model.standart.Properties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertiesRepo extends JpaRepository<Properties, Integer> {
}
