package com.stockkingdom.stock;

import com.stockkingdom.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 순수 시장 데이터만 다루는 엔티티. 게임 도메인(왕국) 개념은 Kingdom에서 별도로 관리한다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stock")
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String ticker;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentPrice;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal marketCap;

    @Builder
    private Stock(String ticker, String companyName, BigDecimal currentPrice, BigDecimal marketCap) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
    }

    public void updatePrice(BigDecimal newPrice) {
        this.currentPrice = newPrice;
    }

    public void updateMarketCap(BigDecimal newMarketCap) {
        this.marketCap = newMarketCap;
    }
}
