package com.gerrnbutton.repository.espi;

import com.gerrnbutton.entity.Authorization;
import com.gerrnbutton.entity.espi.UsagePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("usagePointRepository")
public interface UsagePointRepository extends JpaRepository<UsagePoint, Long> {
    UsagePoint findByAuthorization(Authorization authorization);
}