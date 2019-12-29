package com.gerrnbutton.repository.espi;

import com.gerrnbutton.entity.espi.IntervalDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("intervalDayRepository")
public interface IntervalDayRepository extends JpaRepository<IntervalDay, Long> {

    @Query(nativeQuery = false, value = " SELECT i FROM IntervalDay i WHERE year = ?1 and month = ?2 and day = ?3")
    IntervalDay findByDate(String year, String month, String day);
}

