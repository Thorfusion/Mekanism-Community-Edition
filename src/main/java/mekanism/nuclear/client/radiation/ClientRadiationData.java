package mekanism.nuclear.client.radiation;

import mekanism.nuclear.common.radiation.RadiationManager;

/** Last server-authoritative radiation snapshot received by this client. */
public final class ClientRadiationData {

    private static double environmental = RadiationManager.BASELINE;
    private static double dose = RadiationManager.BASELINE;

    private ClientRadiationData() {
    }

    public static void set(double environmental, double dose) {
        ClientRadiationData.environmental = sanitize(environmental);
        ClientRadiationData.dose = sanitize(dose);
    }

    public static double getEnvironmental() {
        return environmental;
    }

    public static double getDose() {
        return dose;
    }

    public static float getEnvironmentalScale() {
        double level = environmental;
        if (level < 0.000_010D) {
            return 0;
        } else if (level < 0.001D) {
            return 1;
        } else if (level < 0.1D) {
            return 2;
        } else if (level < 10D) {
            return 3;
        } else if (level < 100D) {
            return 4;
        }
        return 5;
    }

    public static void reset() {
        environmental = RadiationManager.BASELINE;
        dose = RadiationManager.BASELINE;
    }

    private static double sanitize(double value) {
        return Double.isNaN(value) || value < RadiationManager.BASELINE
              ? RadiationManager.BASELINE : Math.min(Double.MAX_VALUE, value);
    }
}
