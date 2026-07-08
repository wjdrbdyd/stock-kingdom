package com.stockkingdom.kingdom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stockkingdom.kingdom.dto.KingdomDetailResponse;
import com.stockkingdom.kingdom.dto.KingdomRankingResponse;
import com.stockkingdom.kingdom.dto.QKingdomDetailResponse;
import com.stockkingdom.kingdom.dto.QKingdomRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.stockkingdom.kingdom.QKingdom.kingdom;
import static com.stockkingdom.kingdom.QKingdomPowerSnapshot.kingdomPowerSnapshot;
import static com.stockkingdom.stock.QStock.stock;

@Repository
@RequiredArgsConstructor
public class KingdomPowerSnapshotQueryRepository {
    private final JPAQueryFactory queryFactory;


    public List<KingdomRankingResponse> findRankingByDate(LocalDate now) {
        return queryFactory
                .select(new QKingdomRankingResponse(
                        kingdom.id,
                        kingdom.name,
                        stock.ticker,
                        kingdomPowerSnapshot.power,
                        kingdomPowerSnapshot.rank,
                        kingdomPowerSnapshot.rankChange
                ))
                .from(kingdomPowerSnapshot)
                .join(kingdomPowerSnapshot.kingdom, kingdom)
                .join(kingdom.stock, stock)
                .where(kingdomPowerSnapshot.snapshotDate.eq(now))
                .orderBy(kingdomPowerSnapshot.power.asc())
                .fetch();

    }

    public KingdomDetailResponse findKingdomDetailById(Long kingdomId, LocalDate now) {
        return queryFactory
                .select(new QKingdomDetailResponse(
                        kingdom.id,
                        kingdom.name,
                        stock.ticker,
                        kingdomPowerSnapshot.participantCount,
                        kingdomPowerSnapshot.totalHoldingQuantity,
                        kingdomPowerSnapshot.power,
                        kingdomPowerSnapshot.rank
                ))
                .from(kingdomPowerSnapshot)
                .join(kingdomPowerSnapshot.kingdom, kingdom)
                .join(kingdom.stock, stock)
                .where(
                        kingdomPowerSnapshot.snapshotDate.eq(now),
                        kingdom.id.eq(kingdomId)
                )
                .fetchOne();
    }
}
