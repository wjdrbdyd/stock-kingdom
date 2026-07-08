package com.stockkingdom.kingdom.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.stockkingdom.kingdom.KingdomPowerSnapshot;

import java.math.BigDecimal;

public record KingdomDetailResponse(
        Long kingdomId,
        String kingdomName,
        String ticker,
        int participantCount,
        Long totalHoldingQuantity,
        BigDecimal power,
        int rank
) {
    @QueryProjection
    public KingdomDetailResponse {}

    public static KingdomDetailResponse from(KingdomPowerSnapshot snapshot) {
        return new KingdomDetailResponse(
                snapshot.getKingdom().getId(),
                snapshot.getKingdom().getName(),
                snapshot.getKingdom().getStock().getTicker(),
                snapshot.getParticipantCount(),
                snapshot.getTotalHoldingQuantity(),
                snapshot.getPower(),
                snapshot.getRank()
        );
    }
}