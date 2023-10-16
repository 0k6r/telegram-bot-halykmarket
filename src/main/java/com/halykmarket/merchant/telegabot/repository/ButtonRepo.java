package com.halykmarket.merchant.telegabot.repository;

import com.halykmarket.merchant.telegabot.model.standart.Button;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ButtonRepo extends JpaRepository<Button, Integer> {
    Optional<Button> findByNameAndLangId(String buttonName, int languageId);

    Optional<Button> findByIdAndLangId(int id, int languageId);

    int countByNameAndLangId(String name, int languageId);

    @Transactional
    @Modifying
    @Query("update Button set name = ?1, url = ?2 where id = ?3 and langId = ?4")
    void update(String name, String url, int id, int languageId);
}
