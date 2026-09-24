package mekanism.nuclear.common.radiation;

import net.minecraft.util.DamageSource;

/** Damage types owned by the Nuclear module. */
public final class NuclearDamageSources {

    public static final DamageSource RADIATION = new DamageSource("mekanismnuclear.radiation")
          .setDamageBypassesArmor()
          .setDamageIsAbsolute();

    private NuclearDamageSources() {
    }
}
