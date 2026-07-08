package com.stockkingdom.kingdom.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.stockkingdom.kingdom.KingdomPowerSnapshot;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

public record KingdomRankingResponse(
        Long kingdomId,
        String kingdomName,
        String ticker,
        BigDecimal power,
        int rank,
        int rankChange
) {
    @QueryProjection
    public KingdomRankingResponse {}

    public static KingdomRankingResponse from(KingdomPowerSnapshot snapshot) {
        return new KingdomRankingResponse(
                snapshot.getKingdom().getId(),
                snapshot.getKingdom().getName(),
                snapshot.getKingdom().getStock().getTicker(),
                snapshot.getPower(),
                snapshot.getRank(),
                snapshot.getRankChange()
        );
    }

}
