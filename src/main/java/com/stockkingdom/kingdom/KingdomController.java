package com.stockkingdom.kingdom;

import com.stockkingdom.kingdom.dto.KingdomDetailResponse;
import com.stockkingdom.kingdom.dto.KingdomRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class KingdomController {
    private final KingdomService kingdomService;
    private final KingdomPowerBatchService kingdomPowerBatchService;
    @PostMapping("/admin/batch/run")
    public void runBatch() throws Exception{
        kingdomPowerBatchService.calculateAndSaveSnapshots();
        kingdomPowerBatchService.calculateRanks();
    }

    @GetMapping("/kingdoms/ranking")
    public List<KingdomRankingResponse> findRankingByDate() {
        return kingdomService.findRankingByDate();
    }

    @GetMapping("/kingdoms/{kingdomId}")
    public KingdomDetailResponse findKingdomDetailById(@PathVariable Long kingdomId) {
        return kingdomService.findKingdomDetailById(kingdomId);
    }
}
