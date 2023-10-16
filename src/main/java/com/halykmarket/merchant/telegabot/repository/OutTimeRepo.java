package com.halykmarket.merchant.telegabot.repository;

import com.halykmarket.merchant.telegabot.model.standart.OutTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OutTimeRepo extends JpaRepository<OutTime, Integer> {

    @Query("SELECT o FROM OutTime o WHERE o.createdDate >= :startDate AND o.createdDate <= :endDate ORDER BY o.createdDate ASC")
    List<OutTime> findComeTimeInDateRangeOrderByDateAsc(@Param("startDate") LocalDate start, @Param("endDate") LocalDate end);

    @Query("SELECT o FROM OutTime o WHERE o.createdDate >= :startDate AND o.createdDate <= :endDate AND o.user.id = :userId")
    List<OutTime> findComeTimeInDateRangeAndByUserId(@Param("startDate") LocalDate start,   @Param("endDate") LocalDate end,  @Param("userId") long userId);

}
