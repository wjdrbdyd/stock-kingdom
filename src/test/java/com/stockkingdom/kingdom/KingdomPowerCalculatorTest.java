package com.stockkingdom.kingdom;

import com.stockkingdom.holding.KingdomPowerInput;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KingdomPowerCalculatorTest {

    private final KingdomPowerCalculator calculator = new KingdomPowerCalculator();

    @Test
    void 참여율과_로그참여자수를_곱해서_전투력을_계산한다() {
        // 참여율 = (10 * 100) / 10000 = 0.1, log(참여자수+1) = log(4) = 1.3862943611...
        KingdomPowerInput input = new KingdomPowerInput(
                1L, 10L, 3, BigDecimal.valueOf(100), BigDecimal.valueOf(10_000), 1L);

        BigDecimal power = calculator.calculate(input);

        BigDecimal expectedParticipationRate = BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(10))
                .divide(BigDecimal.valueOf(10_000), MathContext.DECIMAL64);
        BigDecimal expected = expectedParticipationRate
                .multiply(BigDecimal.valueOf(Math.log(4)), MathContext.DECIMAL64)
                .multiply(BigDecimal.valueOf(1_000_000));

        assertThat(power.doubleValue()).isCloseTo(expected.doubleValue(), org.assertj.core.data.Offset.offset(0.0001));
    }

    @Test
    void 참여자가_없으면_전투력은_0이다() {
        KingdomPowerInput input = new KingdomPowerInput(
                1L, 0L, 0, BigDecimal.valueOf(100), BigDecimal.valueOf(10_000), 1L);

        BigDecimal power = calculator.calculate(input);

        assertThat(power.doubleValue()).isEqualTo(0.0);
    }

    @Test
    void 시가총액이_0이하면_예외() {
        KingdomPowerInput input = new KingdomPowerInput(
                1L, 10L, 3, BigDecimal.valueOf(100), BigDecimal.ZERO, 1L);

        assertThatThrownBy(() -> calculator.calculate(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 참여자수가_음수면_예외() {
        KingdomPowerInput input = new KingdomPowerInput(
                1L, 10L, -1, BigDecimal.valueOf(100), BigDecimal.valueOf(10_000), 1L);

        assertThatThrownBy(() -> calculator.calculate(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 보유수량_총합이_음수면_예외() {
        KingdomPowerInput input = new KingdomPowerInput(
                1L, -10L, 3, BigDecimal.valueOf(100), BigDecimal.valueOf(10_000), 1L);

        assertThatThrownBy(() -> calculator.calculate(input))
                .isInstanceOf(IllegalArgumentException.class);
    }
}