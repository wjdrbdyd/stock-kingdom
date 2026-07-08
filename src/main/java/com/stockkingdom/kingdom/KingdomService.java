package com.stockkingdom.kingdom;

import com.stockkingdom.holding.UserStockHoldingQueryRepository;
import com.stockkingdom.kingdom.dto.KingdomDetailResponse;
import com.stockkingdom.kingdom.dto.KingdomRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KingdomService {

    private final KingdomRepository kingdomRepository;
    private final KingdomPowerSnapshotQueryRepository kingdomPowerSnapshotQueryRepository;
    private final KingdomPowerSnapshotRepository kingdomPowerSnapshotRepository;


    public List<KingdomRankingResponse> findRankingByDate() {
//        findBySnapshotDateOrderByRankAsc
        return kingdomPowerSnapshotQueryRepository.findRankingByDate(LocalDate.now());
    }

    public KingdomDetailResponse findKingdomDetailById(Long kingdomId) {
        return kingdomPowerSnapshotQueryRepository.findKingdomDetailById(kingdomId, LocalDate.now());
    }
}
