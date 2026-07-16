package com.stockkingdom.holding;

import com.stockkingdom.kingdom.Kingdom;
import com.stockkingdom.kingdom.KingdomRepository;
import com.stockkingdom.stock.Stock;
import com.stockkingdom.stock.StockRepository;
import com.stockkingdom.user.User;
import com.stockkingdom.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserStockHoldingQueryRepositoryTest {
    @Autowired
    private UserStockHoldingQueryRepository userStockHoldingQueryRepository;
    @Autowired
    private UserStockHoldingRepository userStockHoldingRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private KingdomRepository kingdomRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void 종목별_보유수량과_참여자수를_집계한다() {
        Stock stock = stockRepository.save(Stock.builder()
                .ticker("005930")
                .companyName("삼성전자")
                .currentPrice(BigDecimal.valueOf(70_000))
                .marketCap(BigDecimal.valueOf(1_000_000_000))
                .build());
        Kingdom kingdom = kingdomRepository.save(Kingdom.builder()
                .stock(stock)
                .name("삼성전자 왕국")
                .build());
        User user1 = userRepository.save(User.builder().nickname("user1").email("user1@test.com").build());
        User user2 = userRepository.save(User.builder().nickname("user2").email("user2@test.com").build());
        userStockHoldingRepository.save(UserStockHolding.from(user1, stock, 10L));
        userStockHoldingRepository.save(UserStockHolding.from(user2, stock, 20L));

        List<KingdomPowerInput> aggregates = userStockHoldingQueryRepository.stockHoldingSumAggregate();

        Optional<KingdomPowerInput> found = aggregates.stream()
                .filter(input -> input.stockId().equals(stock.getId()))
                .findFirst();

        assertThat(found).isPresent();
        KingdomPowerInput input = found.get();
        assertThat(input.totalHoldingQuantity()).isEqualTo(30L);
        assertThat(input.participantCount()).isEqualTo(2);
        assertThat(input.kingdomId()).isEqualTo(kingdom.getId());
        assertThat(input.currentPrice()).isEqualByComparingTo(stock.getCurrentPrice());
        assertThat(input.marketCap()).isEqualByComparingTo(stock.getMarketCap());
    }
}