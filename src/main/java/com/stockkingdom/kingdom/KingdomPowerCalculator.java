package com.stockkingdom.kingdom;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 왕국 전투력 계산 도메인 서비스.
 *
 * 원 지표(확정): 참여 유저 수 × 유저들의 보유 주식 총합 × 현재 주가
 * 정규화 방식(미정): 시가총액 격차를 줄이기 위한 스케일링 - min-max 정규화 vs 로그 스케일 등
 *                  비교 후 결정 예정 (설계 결정 문서 참고)
 *
 * TODO: 정규화 방식 확정 후 구현
 */
@Component
public class KingdomPowerCalculator {

    public BigDecimal calculate(int participantCount, long totalHoldingQuantity, BigDecimal currentPrice) {
        throw new UnsupportedOperationException("전투력 정규화 방식 미확정 - 설계 결정 후 구현 필요");
    }
}
