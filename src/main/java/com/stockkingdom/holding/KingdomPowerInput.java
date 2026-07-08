package com.stockkingdom.holding;

import com.querydsl.core.annotations.QueryProjection;

import java.math.BigDecimal;

public record KingdomPowerInput(
        Long stockId,
        Long totalHoldingQuantity,
        Integer participantCount,
        BigDecimal currentPrice,
        BigDecimal marketCap,
        Long kingdomId

) {
    @QueryProjection
    public KingdomPowerInput {}
}

