package com.stockkingdom.holding;

import com.stockkingdom.common.BaseTimeEntity;
import com.stockkingdom.stock.Stock;
import com.stockkingdom.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "user_stock_holding",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stock_id"})
)
public class UserStockHolding extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private Long quantity;

    @Builder
    private UserStockHolding(User user, Stock stock, Long quantity) {
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;
    }

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
