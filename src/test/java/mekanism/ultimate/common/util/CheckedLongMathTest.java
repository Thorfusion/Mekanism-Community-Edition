package mekanism.ultimate.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CheckedLongMathTest {

    @Test
    public void validArithmeticKeepsExactValues() {
        assertEquals(Long.MAX_VALUE, CheckedLongMath.add(Long.MAX_VALUE - 4, 4));
        assertEquals(7, CheckedLongMath.subtract(12, 5));
        assertEquals(Long.MAX_VALUE - 1, CheckedLongMath.multiply((Long.MAX_VALUE - 1) / 2, 2));
        assertEquals(4, CheckedLongMath.ceilDivide(10, 3));
        assertEquals(3, CheckedLongMath.ceilDivide(9, 3));
    }

    @Test(expected = ArithmeticException.class)
    public void additionRejectsOverflow() {
        CheckedLongMath.add(Long.MAX_VALUE, 1);
    }

    @Test(expected = ArithmeticException.class)
    public void multiplicationRejectsOverflow() {
        CheckedLongMath.multiply(Long.MAX_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void subtractionRejectsUnderflow() {
        CheckedLongMath.subtract(1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void domainRejectsNegativeValues() {
        CheckedLongMath.add(-1, 1);
    }

    @Test
    public void legacyIntBoundarySaturates() {
        assertEquals(42, CheckedLongMath.saturatingInt(42));
        assertEquals(Integer.MAX_VALUE, CheckedLongMath.saturatingInt((long) Integer.MAX_VALUE + 1));
    }
}
