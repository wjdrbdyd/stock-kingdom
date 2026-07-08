package com.stockkingdom.holding.dto;

public record HoldingCreateResponse(
        Long holdingId,
        Long userId,
        Long stockId,
        Long quantity
) {

}

