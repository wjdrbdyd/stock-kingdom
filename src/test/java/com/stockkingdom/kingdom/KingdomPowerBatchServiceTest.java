package com.stockkingdom.kingdom;

import com.stockkingdom.holding.UserStockHolding;
import com.stockkingdom.holding.UserStockHoldingRepository;
import com.stockkingdom.stock.Stock;
import com.stockkingdom.stock.StockRepository;
import com.stockkingdom.user.User;
import com.stockkingdom.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class KingdomPowerBatchServiceTest {

    @Autowired
    private KingdomPowerBatchService kingdomPowerBatchService;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private KingdomRepository kingdomRepository;
    @Autowired
    private KingdomPowerSnapshotRepository kingdomPowerSnapshotRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStockHoldingRepository userStockHoldingRepository;

    @Test
    void 어제보다_순위가_오르면_rankChange가_양수다() {
        Kingdom risingKingdom = createKingdom("TEST-001", "테스트종목1");
        Kingdom fallingKingdom = createKingdom("TEST-002", "테스트종목2");

        // 어제: risingKingdom 2등, fallingKingdom 1등
        saveSnapshot(risingKingdom, LocalDate.now().minusDays(1), BigDecimal.valueOf(10), 2, null);
        saveSnapshot(fallingKingdom, LocalDate.now().minusDays(1), BigDecimal.valueOf(20), 1, null);

        // 오늘: risingKingdom이 1등으로 역전
        saveSnapshot(risingKingdom, LocalDate.now(), BigDecimal.valueOf(30), null, null);
        saveSnapshot(fallingKingdom, LocalDate.now(), BigDecimal.valueOf(20), null, null);

        kingdomPowerBatchService.calculateRanks();

        KingdomPowerSnapshot risingToday = findTodaySnapshot(risingKingdom);
        KingdomPowerSnapshot fallingToday = findTodaySnapshot(fallingKingdom);

        assertThat(risingToday.getRank()).isEqualTo(1);
        assertThat(risingToday.getRankChange()).isEqualTo(1); // 2등 -> 1등, +1

        assertThat(fallingToday.getRank()).isEqualTo(2);
        assertThat(fallingToday.getRankChange()).isEqualTo(-1); // 1등 -> 2등, -1
    }

    @Test
    void 재실행해도_오늘_스냅샷은_왕국당_1건만_남는다() throws Exception {
        Kingdom kingdom = createKingdom("TEST-003", "테스트종목3");
        User user = userRepository.save(User.builder().nickname("tester").email("tester-" + System.nanoTime() + "@test.com").build());
        userStockHoldingRepository.save(UserStockHolding.from(user, kingdom.getStock(), 10L));

        kingdomPowerBatchService.calculateAndSaveSnapshots();
        long firstRunTotal = kingdomPowerSnapshotRepository.findBySnapshotDate(LocalDate.now()).size();
        long firstRunForKingdom = countSnapshotsForKingdom(kingdom);

        kingdomPowerBatchService.calculateAndSaveSnapshots();
        long secondRunTotal = kingdomPowerSnapshotRepository.findBySnapshotDate(LocalDate.now()).size();
        long secondRunForKingdom = countSnapshotsForKingdom(kingdom);

        assertThat(firstRunForKingdom).isEqualTo(1);
        assertThat(secondRunForKingdom).isEqualTo(1);
        assertThat(secondRunTotal).isEqualTo(firstRunTotal);
    }

    private long countSnapshotsForKingdom(Kingdom kingdom) {
        return kingdomPowerSnapshotRepository.findBySnapshotDate(LocalDate.now()).stream()
                .filter(s -> s.getKingdom().getId().equals(kingdom.getId()))
                .count();
    }

    private Kingdom createKingdom(String ticker, String companyName) {
        Stock stock = stockRepository.save(Stock.builder()
                .ticker(ticker)
                .companyName(companyName)
                .currentPrice(BigDecimal.valueOf(1000))
                .marketCap(BigDecimal.valueOf(1_000_000))
                .build());
        return kingdomRepository.save(Kingdom.builder()
                .stock(stock)
                .name(companyName + " 왕국")
                .build());
    }

    private void saveSnapshot(Kingdom kingdom, LocalDate date, BigDecimal power, Integer rank, Integer rankChange) {
        KingdomPowerSnapshot snapshot = KingdomPowerSnapshot.toEntity(kingdom, 100L, 1, power);
        if (rank != null) {
            snapshot.assignRank(rank, rankChange == null ? 0 : rankChange);
        }
        setSnapshotDate(snapshot, date);
        kingdomPowerSnapshotRepository.save(snapshot);
    }

    private void setSnapshotDate(KingdomPowerSnapshot snapshot, LocalDate date) {
        try {
            var field = KingdomPowerSnapshot.class.getDeclaredField("snapshotDate");
            field.setAccessible(true);
            field.set(snapshot, date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private KingdomPowerSnapshot findTodaySnapshot(Kingdom kingdom) {
        return kingdomPowerSnapshotRepository.findBySnapshotDate(LocalDate.now()).stream()
                .filter(s -> s.getKingdom().getId().equals(kingdom.getId()))
                .findFirst()
                .orElseThrow();
    }
}