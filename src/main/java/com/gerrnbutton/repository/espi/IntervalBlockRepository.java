package com.gerrnbutton.repository.espi;

import com.gerrnbutton.entity.espi.IntervalBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("intervalBlockRepository")
public interface IntervalBlockRepository extends JpaRepository<IntervalBlock, Long> {
}
