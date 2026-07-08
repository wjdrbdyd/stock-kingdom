package com.stockkingdom.holding;

import com.stockkingdom.stock.Stock;
import com.stockkingdom.stock.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class UserStockHoldingQueryRepositoryTest {
    @Autowired
    private UserStockHoldingQueryRepository userStockHoldingQueryRepository;
    @Autowired
    private StockRepository stockRepository;
    @Test
    void aggregateTest() {
        List<KingdomPowerInput> stockHoldingAggregates = userStockHoldingQueryRepository.stockHoldingSumAggregate();
        stockHoldingAggregates.forEach(
                stockHoldingAggregate -> {
                    Long stockId = stockHoldingAggregate.stockId();
                    Stock byId = stockRepository.findById(stockId).get();

                    System.out.println("getCompanyName() = "
                            + byId.getCompanyName().concat(", "));
                }
        );
    }
}