package mekanism.nuclear.common.radiation;

/** Formatting and decay estimates shared by Nuclear radiation instruments. */
public final class RadiationDisplay {

    private static final String[] PREFIXES = {"f", "p", "n", "u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y"};
    private static final int MIN_GROUP = -5;
    private static final int MAX_GROUP = 8;

    private RadiationDisplay() {
    }

    public static String formatDose(double sieverts) {
        return format(sieverts, "Sv");
    }

    public static String formatDoseRate(double sievertsPerHour) {
        return format(sievertsPerHour, "Sv/h");
    }

    private static String format(double value, String unit) {
        if (Double.isNaN(value) || value < 0) {
            value = 0;
        }
        if (Double.isInfinite(value)) {
            return "infinite " + unit;
        }
        int group = value == 0 ? 0 : (int) Math.floor(Math.log10(value) / 3D);
        group = Math.max(MIN_GROUP, Math.min(MAX_GROUP, group));
        double scaled = value / Math.pow(1_000D, group);
        String prefix = PREFIXES[group - MIN_GROUP];
        double rounded = Math.round(scaled * 1_000D) / 1_000D;
        return rounded + " " + prefix + unit;
    }

    /** Number of seconds until a dose or source drops below the gameplay threshold. */
    public static long getDecaySeconds(double magnitude, double decayRate) {
        if (magnitude <= RadiationManager.MIN_MAGNITUDE) {
            return 0;
        }
        if (decayRate <= 0) {
            return 1;
        }
        if (decayRate >= 1 || Double.isNaN(decayRate)) {
            return Long.MAX_VALUE;
        }
        double seconds = Math.ceil(Math.log(RadiationManager.MIN_MAGNITUDE / magnitude) / Math.log(decayRate));
        return Double.isInfinite(seconds) || seconds >= Long.MAX_VALUE ? Long.MAX_VALUE : Math.max(1, (long) seconds);
    }

    public static String formatDuration(long seconds) {
        if (seconds == Long.MAX_VALUE) {
            return "never";
        }
        long hours = seconds / 3_600;
        long minutes = seconds % 3_600 / 60;
        long remainingSeconds = seconds % 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + remainingSeconds + "s";
        }
        return remainingSeconds + "s";
    }
}
