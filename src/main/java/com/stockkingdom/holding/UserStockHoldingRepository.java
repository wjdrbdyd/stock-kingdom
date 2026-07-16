package com.stockkingdom.holding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserStockHoldingRepository extends JpaRepository<UserStockHolding, Long> {
    // 커스텀 조회(예: 종목별 보유 합계 집계)는 QueryDSL로 UserStockHoldingRepositoryCustom에 추가 예정
    List<UserStockHolding> findByStockId(Long stockId);
    Optional<UserStockHolding> findByUserIdAndStockId(Long userId, Long stockId);
}
