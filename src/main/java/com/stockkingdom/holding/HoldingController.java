package com.stockkingdom.holding;

import com.stockkingdom.holding.dto.HoldingCreateRequest;
import com.stockkingdom.holding.dto.HoldingCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HoldingController {
    private final HoldingService holdingService;
    @PostMapping("/holdings")
    public HoldingCreateResponse saveHoldings(@RequestBody HoldingCreateRequest request) {
        return holdingService.saveHoldings(request);
    }
}
