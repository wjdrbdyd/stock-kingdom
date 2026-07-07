package com.stockkingdom.kingdom;

import com.stockkingdom.common.BaseTimeEntity;
import com.stockkingdom.stock.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 종목을 소유한 유저들의 모임(게임 도메인). 현재는 Stock과 1:1이지만,
 * 추후 섹터 통합전 도입 시 여러 Stock을 포함하는 구조로 확장 가능하도록
 * Stock과 분리된 엔티티로 둔다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "kingdom")
public class Kingdom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false, unique = true)
    private Stock stock;

    @Column(nullable = false, length = 50)
    private String name;

    @Builder
    private Kingdom(Stock stock, String name) {
        this.stock = stock;
        this.name = name;
    }
}
