package com.stockkingdom.kingdom;

import com.stockkingdom.holding.KingdomPowerInput;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * 왕국 전투력 계산 도메인 서비스.
 *
 * 원 지표: 참여 유저 수 × 유저들의 보유 주식 총합 × 현재 주가
 * 정규화 방식(확정): power = 참여율 × log(참여자 수 + 1)
 *   - 참여율 = (유저 보유 주식 총합 × 현재가) / 종목 시가총액
 *   - 시가총액을 분모로 두어 회사 규모와 무관하게 경쟁 가능하게 하고,
 *     log(참여자 수 + 1)로 소수 유저 몰빵 왜곡을 방지 (요구사항정의서 v1, 5장 참고)
 */
@Component
public class KingdomPowerCalculator {

    public BigDecimal calculate(KingdomPowerInput powerInput) {
        long participantCount = powerInput.participantCount();
        long totalHoldingQuantity = powerInput.totalHoldingQuantity();
        BigDecimal currentPrice = powerInput.currentPrice();
        BigDecimal marketCap = powerInput.marketCap();

        if (marketCap == null || marketCap.signum() <= 0) {
            throw new IllegalArgumentException("marketCap must be positive: " + marketCap);
        }
        if (participantCount < 0) {
            throw new IllegalArgumentException("participantCount must not be negative: " + participantCount);
        }
        if (totalHoldingQuantity < 0) {
            throw new IllegalArgumentException("totalHoldingQuantity must not be negative: " + totalHoldingQuantity);
        }

        // 1. 참여율 계산: (유저 보유 주식 총합 × 현재가) / 종목 시가총액
        BigDecimal totalHoldingValue = currentPrice.multiply(BigDecimal.valueOf(totalHoldingQuantity));
        BigDecimal participationRate = totalHoldingValue.divide(marketCap, MathContext.DECIMAL64); // 정밀도 유지를 위해 MathContext 사용

        // 2. log(참여자 수 + 1) 계산
        // participantCount가 0일 경우 log(1)은 0이 되며, 이는 합리적인 결과입니다.
        double logParticipants = Math.log(participantCount + 1);

        // 3. 최종 전투력 계산: 참여율 × log(참여자 수 + 1)
        // double 타입의 logParticipants를 BigDecimal로 변환하여 곱셈
        BigDecimal power = participationRate.multiply(BigDecimal.valueOf(logParticipants), MathContext.DECIMAL64)
                                        .multiply(BigDecimal.valueOf(1_000_000));  // 이 줄만 추가
        return power;
    }
}
