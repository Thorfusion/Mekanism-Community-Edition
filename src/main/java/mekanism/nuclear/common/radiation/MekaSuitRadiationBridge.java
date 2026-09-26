package mekanism.nuclear.common.radiation;

import java.lang.reflect.Method;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

/** Nuclear-owned optional bridge; the Nuclear artifact never requires MekaSuit. */
public final class MekaSuitRadiationBridge {

    private static final String INTEGRATION_CLASS =
          "mekanism.mekasuit.common.content.gear.MekaSuitRadiationIntegration";
    private static Method shielding;
    private static Method dosimeter;
    private static Method geiger;
    private static boolean resolved;

    private MekaSuitRadiationBridge() {
    }

    public static double getShielding(ItemStack stack) {
        Object value = invoke(shieldingMethod(), stack);
        return value instanceof Number ? Math.max(0, Math.min(1, ((Number) value).doubleValue())) : 0;
    }

    public static boolean hasDosimeter(ItemStack stack) {
        return Boolean.TRUE.equals(invoke(dosimeterMethod(), stack));
    }

    public static boolean hasGeiger(ItemStack stack) {
        return Boolean.TRUE.equals(invoke(geigerMethod(), stack));
    }

    private static Method shieldingMethod() {
        resolve();
        return shielding;
    }

    private static Method dosimeterMethod() {
        resolve();
        return dosimeter;
    }

    private static Method geigerMethod() {
        resolve();
        return geiger;
    }

    private static synchronized void resolve() {
        if (resolved) {
            return;
        }
        resolved = true;
        if (!Loader.isModLoaded("mekanismmekasuit")) {
            return;
        }
        try {
            Class<?> integration = Class.forName(INTEGRATION_CLASS);
            shielding = integration.getMethod("getShielding", ItemStack.class);
            dosimeter = integration.getMethod("hasDosimeter", ItemStack.class);
            geiger = integration.getMethod("hasGeiger", ItemStack.class);
        } catch (ReflectiveOperationException ignored) {
            shielding = dosimeter = geiger = null;
        }
    }

    private static Object invoke(Method method, ItemStack stack) {
        if (method == null || stack == null || stack.isEmpty()) {
            return null;
        }
        try {
            return method.invoke(null, stack);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
