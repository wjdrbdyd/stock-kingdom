package com.stockkingdom.holding;

import com.stockkingdom.holding.dto.HoldingCreateRequest;
import com.stockkingdom.holding.dto.HoldingCreateResponse;
import com.stockkingdom.stock.Stock;
import com.stockkingdom.stock.StockRepository;
import com.stockkingdom.user.User;
import com.stockkingdom.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HoldingService {
    private final UserStockHoldingRepository userStockHoldingRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public HoldingCreateResponse saveHoldings(HoldingCreateRequest request) {
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + request.quantity());
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("user not found: " + request.userId()));
        Stock stock = stockRepository.findById(request.stockId())
                .orElseThrow(() -> new EntityNotFoundException("stock not found: " + request.stockId()));

        UserStockHolding holding = userStockHoldingRepository.findByUserIdAndStockId(user.getId(), stock.getId())
                .map(existing -> {
                    existing.updateQuantity(request.quantity());
                    return existing;
                })
                .orElseGet(() -> userStockHoldingRepository.save(UserStockHolding.from(user, stock, request.quantity())));

        return new HoldingCreateResponse(
                holding.getId(),
                request.userId(),
                request.stockId(),
                request.quantity()
        );
    }
}
