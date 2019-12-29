package com.gerrnbutton.repository;

import com.gerrnbutton.entity.Authorization;
import com.gerrnbutton.entity.EnergyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("energyDataRepository")
public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {
    EnergyData findByAuthorization(Authorization authorization);
}