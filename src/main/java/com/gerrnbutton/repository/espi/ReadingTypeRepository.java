package com.gerrnbutton.repository.espi;

import com.gerrnbutton.entity.espi.ReadingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("readingTypeRepository")
public interface ReadingTypeRepository extends JpaRepository<ReadingType, Long> {
}