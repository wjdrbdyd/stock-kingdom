package com.stockkingdom.holding;

import com.stockkingdom.holding.dto.HoldingCreateRequest;
import com.stockkingdom.holding.dto.HoldingCreateResponse;
import com.stockkingdom.stock.Stock;
import com.stockkingdom.stock.StockRepository;
import com.stockkingdom.user.User;
import com.stockkingdom.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HoldingService {
    private final UserStockHoldingRepository userStockHoldingRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public HoldingCreateResponse saveHoldings(HoldingCreateRequest request) {

        User refUser = userRepository.getReferenceById(request.userId());
        Stock refStock = stockRepository.getReferenceById(request.stockId());
        UserStockHolding from = UserStockHolding.from(refUser, refStock, request.quantity());
        UserStockHolding saveHolding = userStockHoldingRepository.save(from);

        return new HoldingCreateResponse(
                saveHolding.getId(),
                request.userId(),
                request.stockId(),
                request.quantity()
        );
    }
}
