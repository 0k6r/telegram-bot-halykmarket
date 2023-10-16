package com.halykmarket.merchant.telegabot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.halykmarket.merchant.telegabot.model.standart.Button;

@Repository
public interface ButtonRepo extends CrudRepository<Button, Integer> {
    Button  findByNameAndLangId(String buttonName, int languageId);
    Button  findByIdAndLangId(int id, int langId);
    int     countByNameAndLangId(String name, int langId);
    boolean existsButtonByNameAndLangId(String text, int langId);
    Button   findById(int id);

    @Transactional
    @Modifying
    @Query("update Button set name = ?1, url = ?2 where id = ?3 and langId = ?4")
    void update(String name, String url, int id, int langId);
}
