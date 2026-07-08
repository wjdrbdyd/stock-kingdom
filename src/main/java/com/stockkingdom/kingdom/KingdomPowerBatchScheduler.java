package com.stockkingdom.kingdom;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KingdomPowerBatchScheduler {
    private final KingdomPowerBatchService kingdomPowerBatchService;
    /*
1. @Scheduled로 매일 정해진 시각에 트리거
2. QueryDSL로 종목별 집계(totalQuantity, participantCount) 조회
3. 각 Stock/Kingdom에 대해 KingdomPowerCalculator로 power 계산
4. KingdomPowerSnapshot 저장 (snapshot_date = 오늘)
5. 전체 저장 끝난 후, power 기준 정렬 → rank 매기기
6. 어제 스냅샷과 비교해서 rank_change 계산
     */
    @Scheduled(cron= "2 * * * * *")
    public void runDailyBatch() throws Exception{
        kingdomPowerBatchService.calculateAndSaveSnapshots();
        kingdomPowerBatchService.calculateRanks();
    }
}
