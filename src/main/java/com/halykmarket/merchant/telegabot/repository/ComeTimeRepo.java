package com.halykmarket.merchant.telegabot.repository;

import com.halykmarket.merchant.telegabot.model.standart.ComeTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ComeTimeRepo extends JpaRepository<ComeTime, Integer> {
    @Query("SELECT o FROM ComeTime o WHERE o.createdDate >= :startDate AND o.createdDate <= :endDate " +
            "ORDER BY o.createdDate ASC")
    List<ComeTime> findComeTimeInDateRangeOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    @Query("SELECT o FROM ComeTime o WHERE o.createdDate >= :startDate AND o.createdDate <= :endDate " +
            "AND o.user.id = :userId")
    List<ComeTime> findComeTimeInDateRangeAndByUserId(LocalDate startDate, LocalDate endDate, long userId);
}
