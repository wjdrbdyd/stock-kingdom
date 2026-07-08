package com.stockkingdom.kingdom;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface KingdomPowerSnapshotRepository extends JpaRepository<KingdomPowerSnapshot, Long> {

    List<KingdomPowerSnapshot> findBySnapshotDateOrderByPowerDesc(LocalDate snapshotDate);

    List<KingdomPowerSnapshot> findBySnapshotDate(LocalDate snapshotDate);

    void deleteBySnapshotDate(LocalDate now);
}
