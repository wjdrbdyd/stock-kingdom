package com.stockkingdom.kingdom;

import com.stockkingdom.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KingdomRepository extends JpaRepository<Kingdom, Long> {
    Kingdom findByStock(Stock stockId);
}
