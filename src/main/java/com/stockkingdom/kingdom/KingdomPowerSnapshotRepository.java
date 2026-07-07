package com.stockkingdom.kingdom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface KingdomPowerSnapshotRepository extends JpaRepository<KingdomPowerSnapshot, Long> {

    List<KingdomPowerSnapshot> findBySnapshotDateOrderByPowerDesc(LocalDate snapshotDate);

    Optional<KingdomPowerSnapshot> findByKingdomIdAndSnapshotDate(Long kingdomId, LocalDate snapshotDate);
}
