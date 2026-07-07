package com.stockkingdom.kingdom;

import com.stockkingdom.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 왕국의 일자별 전투력 + 순위 스냅샷.
 * 처음엔 전투력(Power) 계산과 순위(Ranking)를 별도 엔티티로 분리했으나,
 * 순위 산출에 별도 로직이 없고(단순 정렬) 배치 처리 순서 의존성만 있어
 * 하나의 엔티티로 병합함. (설계 결정 문서 참고)
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "kingdom_power_snapshot",
    uniqueConstraints = @UniqueConstraint(columnNames = {"kingdom_id", "snapshot_date"})
)
public class KingdomPowerSnapshot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kingdom_id", nullable = false)
    private Kingdom kingdom;

    @Column(nullable = false)
    private LocalDate snapshotDate;

    @Column(nullable = false)
    private Long totalHoldingQuantity;

    @Column(nullable = false)
    private Integer participantCount;

    // TODO: 전투력 산출 공식(원 지표: 참여 유저 수 × 보유 주식 총합 × 현재가)은 확정,
    //       왕국 간 격차를 줄이는 정규화 방식(min-max, 로그 스케일 등)은 아직 미정.
    //       KingdomPowerCalculator(도메인 서비스)에서 계산 후 이 필드에 저장할 예정.
    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal power;

    // 그날 랭킹에서 계산되기 전까지는 null일 수 있음
    private Integer rank;

    private Integer rankChange;

    @Builder
    private KingdomPowerSnapshot(Kingdom kingdom, LocalDate snapshotDate,
                                  Long totalHoldingQuantity, Integer participantCount,
                                  BigDecimal power) {
        this.kingdom = kingdom;
        this.snapshotDate = snapshotDate;
        this.totalHoldingQuantity = totalHoldingQuantity;
        this.participantCount = participantCount;
        this.power = power;
    }

    public void assignRank(Integer rank, Integer previousRank) {
        this.rank = rank;
        this.rankChange = (previousRank == null) ? 0 : previousRank - rank;
    }
}
