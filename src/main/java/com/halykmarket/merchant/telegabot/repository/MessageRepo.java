package com.halykmarket.merchant.telegabot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.halykmarket.merchant.telegabot.model.standart.Message;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface MessageRepo extends CrudRepository<Message, Integer> {

    Message findFirstByIdAndLanguageId(int id, int languageId);

    @Query("select m from Message m where m.id = ?1 and m.languageId =?2")
    Message findByIdAndLanguageId(int id, int languageId);

    @Query("select m.name from Message m where m.id = :id and m.languageId = :langId")
    String getMessageText(@Param("id") int id, @Param("langId") int langId);

    @Transactional
    @Modifying
    @Query("update Message set name = ?1, photo = ?2, file = ?3, fileType = ?4 where id = ?5 and languageId = ?6")
    void update(String name, String photo, String file, String fileType, int id, int langId);

    @Query("select m.name from Message m WHERE m.id =?1 and m.languageId =?2")
    Optional<String> getName(int id, int langId);
}
