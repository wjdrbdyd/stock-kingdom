package com.stockkingdom.holding;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stockkingdom.kingdom.QKingdom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.stockkingdom.holding.QUserStockHolding.userStockHolding;
import static com.stockkingdom.kingdom.QKingdom.kingdom;
import static com.stockkingdom.stock.QStock.stock;

@Repository
@RequiredArgsConstructor
public class UserStockHoldingQueryRepository {
    private final JPAQueryFactory queryFactory;

//    종목별 보유 합계 집계
    public List<KingdomPowerInput> stockHoldingSumAggregate() {
        return queryFactory
                .select(new QKingdomPowerInput(
                        userStockHolding.stock.id,
                        userStockHolding.quantity.sumLong(),
                        userStockHolding.user.count().intValue(),
                        stock.currentPrice,
                        stock.marketCap,
                        kingdom.id
                ))
                .from(userStockHolding)
                .join(userStockHolding.stock, stock)
                .join(kingdom).on(kingdom.stock.eq(stock))
                .groupBy(userStockHolding.stock.id, stock.currentPrice, stock.marketCap, kingdom.id)
                .orderBy(stock.id.asc())
                .fetch();
    }
}
