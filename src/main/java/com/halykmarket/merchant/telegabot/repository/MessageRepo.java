package com.halykmarket.merchant.telegabot.repository;

import com.halykmarket.merchant.telegabot.model.standart.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepo extends JpaRepository<Message, Integer> {

    @Query("SELECT m FROM Message m WHERE m.id = :id AND m.languageId = :languageId")
    Optional<Message> findByIdAndLanguageId(int id, int languageId);

    @Transactional
    @Modifying
    @Query("UPDATE Message SET name = :name, photo = :photo, file = :file, fileType = :fileType " +
            "WHERE id = :id AND languageId = :languageId")
    void update(String name, String photo, String file, String fileType, int id, int langId);

    @Query("SELECT m.name FROM Message m WHERE m.id = :id AND m.languageId = :languageId")
    Optional<String> getName(int id, int languageId);
}
