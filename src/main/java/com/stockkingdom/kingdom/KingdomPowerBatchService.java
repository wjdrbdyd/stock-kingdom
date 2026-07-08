package com.stockkingdom.kingdom;

import com.stockkingdom.holding.KingdomPowerInput;
import com.stockkingdom.holding.UserStockHoldingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KingdomPowerBatchService {
    private final UserStockHoldingQueryRepository userStockHoldingQueryRepository;
    private final KingdomPowerCalculator kingdomPowerCalculator;
    private final KingdomRepository kingdomRepository;
    private final KingdomPowerSnapshotRepository kingdomPowerSnapshotRepository;

    @Transactional
    public void calculateAndSaveSnapshots() throws Exception {

        // 재실행 대비 : 오늘자 스냅샷 있으면 삭제.
        kingdomPowerSnapshotRepository.deleteBySnapshotDate(LocalDate.now());
        kingdomPowerSnapshotRepository.flush(); // 이거 추가
        // 1. 집계 조회
        List<KingdomPowerInput> stockHoldingAggregates = userStockHoldingQueryRepository.stockHoldingSumAggregate();
        // 2. power 계산 + 저장
        List<KingdomPowerSnapshot> snapshots = stockHoldingAggregates.stream()
                .map(input -> {
                    BigDecimal power = kingdomPowerCalculator.calculate(input);
                    Kingdom kingdom = kingdomRepository.findById(input.kingdomId()).get();
                    return KingdomPowerSnapshot.toEntity(kingdom, input.totalHoldingQuantity()
                            , input.participantCount(), power);

                }).toList();
        kingdomPowerSnapshotRepository.saveAll(snapshots);

    }

    @Transactional
    public void calculateRanks() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        // 1. 오늘 날짜 스냅샷 전체 조회  power 기준 내림차순
        List<KingdomPowerSnapshot> todaySnapshots = kingdomPowerSnapshotRepository.findBySnapshotDateOrderByPowerDesc(today);
        Map<Kingdom, Integer> yesterdayRankMap = kingdomPowerSnapshotRepository
                .findBySnapshotDate(yesterday)
                .stream()
                .collect(toMap(s -> s.getKingdom(), KingdomPowerSnapshot::getRank));
        // 2.  power 기준 정렬 → rank 부여
        for(int i = 0; i < todaySnapshots.size(); i++) {
            int todayRank = i+1;
            KingdomPowerSnapshot todaySnapshot = todaySnapshots.get(i);
            Long kingdomId = todaySnapshot.getKingdom().getId();
            int rankChange  = yesterdayRankMap.getOrDefault(kingdomId, todayRank) - todayRank;

            todaySnapshot.assignRank(todayRank, rankChange);
        }

    }
}
