package com.stockkingdom.holding.dto;

public record HoldingCreateRequest(
        Long userId,
        Long stockId,
        Long quantity
) {}