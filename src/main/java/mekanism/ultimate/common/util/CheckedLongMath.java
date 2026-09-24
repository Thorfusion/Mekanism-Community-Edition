package mekanism.ultimate.common.util;

/** Overflow-checking helpers for non-negative storage and energy arithmetic. */
public final class CheckedLongMath {

    private CheckedLongMath() {
    }

    public static long add(long left, long right) {
        requireNonNegative(left, "left");
        requireNonNegative(right, "right");
        return Math.addExact(left, right);
    }

    public static long subtract(long value, long amount) {
        requireNonNegative(value, "value");
        requireNonNegative(amount, "amount");
        if (amount > value) {
            throw new ArithmeticException("Non-negative subtraction underflow: " + value + " - " + amount);
        }
        return value - amount;
    }

    public static long multiply(long left, long right) {
        requireNonNegative(left, "left");
        requireNonNegative(right, "right");
        return Math.multiplyExact(left, right);
    }

    public static long ceilDivide(long value, long divisor) {
        requireNonNegative(value, "value");
        if (divisor <= 0) {
            throw new IllegalArgumentException("Divisor must be positive: " + divisor);
        }
        long quotient = value / divisor;
        return value % divisor == 0 ? quotient : Math.addExact(quotient, 1);
    }

    public static int saturatingInt(long value) {
        requireNonNegative(value, "value");
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    private static void requireNonNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative: " + value);
        }
    }
}
