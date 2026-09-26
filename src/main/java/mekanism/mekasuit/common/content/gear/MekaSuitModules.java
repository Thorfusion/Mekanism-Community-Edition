package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleExclusive;
import mekanism.mekasuit.api.gear.ModuleRegistry;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.MekanismMekaSuit;
import net.minecraft.util.ResourceLocation;

/** Canonical module definitions from the pinned stable Mekanism release. */
public final class MekaSuitModules {

    private static final ModuleTarget[] ALL_GEAR = ModuleTarget.values();
    private static final ModuleTarget[] ALL_ARMOR = {
          ModuleTarget.HELMET, ModuleTarget.BODYARMOR, ModuleTarget.PANTS, ModuleTarget.BOOTS
    };

    public static final ModuleType ENERGY_UNIT = type("energy_unit", ALL_GEAR).maxInstallCount(8).noDisable().build();
    public static final ModuleType COLOR_MODULATION_UNIT = type("color_modulation_unit", ALL_ARMOR).noDisable().build();
    public static final ModuleType LASER_DISSIPATION_UNIT = type("laser_dissipation_unit", ALL_ARMOR).build();
    public static final ModuleType RADIATION_SHIELDING_UNIT = type("radiation_shielding_unit", ALL_ARMOR).build();

    public static final ModuleType EXCAVATION_ESCALATION_UNIT = type("excavation_escalation_unit", ModuleTarget.MEKA_TOOL)
          .maxInstallCount(4).modes(2, "normal", "off", "slow", "normal", "fast", "super_fast", "extreme").build();
    public static final ModuleType ATTACK_AMPLIFICATION_UNIT = type("attack_amplification_unit", ModuleTarget.MEKA_TOOL)
          .maxInstallCount(4).modes(2, "med", "off", "low", "med", "high", "extreme", "max").build();
    public static final ModuleType FARMING_UNIT = type("farming_unit", ModuleTarget.MEKA_TOOL)
          .maxInstallCount(4).modes(1, "low", "off", "low", "med", "high", "ultra")
          .exclusive(ModuleExclusive.INTERACT_BLOCK).build();
    public static final ModuleType SHEARING_UNIT = type("shearing_unit", ModuleTarget.MEKA_TOOL)
          .exclusive(ModuleExclusive.INTERACT_BLOCK, ModuleExclusive.INTERACT_ENTITY).build();
    public static final ModuleType SILK_TOUCH_UNIT = type("silk_touch_unit", ModuleTarget.MEKA_TOOL)
          .exclusive(ModuleExclusive.OVERRIDE_DROPS).build();
    public static final ModuleType FORTUNE_UNIT = type("fortune_unit", ModuleTarget.MEKA_TOOL)
          .maxInstallCount(3).exclusive(ModuleExclusive.OVERRIDE_DROPS).build();
    public static final ModuleType BLASTING_UNIT = type("blasting_unit", ModuleTarget.MEKA_TOOL)
          .maxInstallCount(4).modes(1, "low", "off", "low", "med", "high", "extreme").build();
    public static final ModuleType VEIN_MINING_UNIT = type("vein_mining_unit", ModuleTarget.MEKA_TOOL)
          .maxInstallCount(4).modes(1, "low", "off", "low", "med", "high", "extreme")
          .booleanConfig("extended", false).build();
    public static final ModuleType TELEPORTATION_UNIT = type("teleportation_unit", ModuleTarget.MEKA_TOOL)
          .booleanConfig("teleportation_requires_block", true)
          .exclusive(ModuleExclusive.INTERACT_BLOCK, ModuleExclusive.INTERACT_ENTITY).build();

    public static final ModuleType ELECTROLYTIC_BREATHING_UNIT = type("electrolytic_breathing_unit", ModuleTarget.HELMET)
          .maxInstallCount(4).booleanConfig("fill_held", true).build();
    public static final ModuleType INHALATION_PURIFICATION_UNIT = type("inhalation_purification_unit", ModuleTarget.HELMET).build();
    public static final ModuleType VISION_ENHANCEMENT_UNIT = type("vision_enhancement_unit", ModuleTarget.HELMET)
          .maxInstallCount(4).handlesModeChange().disabledByDefault().build();
    public static final ModuleType NUTRITIONAL_INJECTION_UNIT = type("nutritional_injection_unit", ModuleTarget.HELMET).build();

    public static final ModuleType DOSIMETER_UNIT = type("dosimeter_unit", ModuleTarget.BODYARMOR).build();
    public static final ModuleType GEIGER_UNIT = type("geiger_unit", ModuleTarget.BODYARMOR).build();
    public static final ModuleType JETPACK_UNIT = type("jetpack_unit", ModuleTarget.BODYARMOR)
          .maxInstallCount(4).handlesModeChange().exclusive(ModuleExclusive.OVERRIDE_JUMP).build();
    public static final ModuleType CHARGE_DISTRIBUTION_UNIT = type("charge_distribution_unit", ModuleTarget.BODYARMOR).build();
    public static final ModuleType GRAVITATIONAL_MODULATING_UNIT = type("gravitational_modulating_unit", ModuleTarget.BODYARMOR)
          .handlesModeChange().exclusive(ModuleExclusive.OVERRIDE_JUMP).build();
    public static final ModuleType ELYTRA_UNIT = type("elytra_unit", ModuleTarget.BODYARMOR)
          .handlesModeChange().disabledByDefault().build();

    public static final ModuleType LOCOMOTIVE_BOOSTING_UNIT = type("locomotive_boosting_unit", ModuleTarget.PANTS)
          .maxInstallCount(4).handlesModeChange().build();
    public static final ModuleType GYROSCOPIC_STABILIZATION_UNIT = type("gyroscopic_stabilization_unit", ModuleTarget.PANTS).build();
    public static final ModuleType HYDROSTATIC_REPULSOR_UNIT = type("hydrostatic_repulsor_unit", ModuleTarget.PANTS)
          .maxInstallCount(4).build();
    public static final ModuleType MOTORIZED_SERVO_UNIT = type("motorized_servo_unit", ModuleTarget.PANTS)
          .maxInstallCount(5).build();

    public static final ModuleType HYDRAULIC_PROPULSION_UNIT = type("hydraulic_propulsion_unit", ModuleTarget.BOOTS)
          .maxInstallCount(4).build();
    public static final ModuleType MAGNETIC_ATTRACTION_UNIT = type("magnetic_attraction_unit", ModuleTarget.BOOTS)
          .maxInstallCount(4).handlesModeChange().build();
    public static final ModuleType FROST_WALKER_UNIT = type("frost_walker_unit", ModuleTarget.BOOTS)
          .maxInstallCount(2).build();
    public static final ModuleType SOUL_SURFER_UNIT = type("soul_surfer_unit", ModuleTarget.BOOTS)
          .maxInstallCount(3).build();

    private static boolean bootstrapped;

    static {
        bootstrap();
    }

    private MekaSuitModules() {
    }

    public static synchronized void bootstrap() {
        if (bootstrapped) {
            return;
        }
        ModuleRegistry registry = ModuleRegistry.getInstance();
        registry.register(ENERGY_UNIT);
        registry.register(COLOR_MODULATION_UNIT);
        registry.register(LASER_DISSIPATION_UNIT);
        registry.register(RADIATION_SHIELDING_UNIT);
        registry.register(EXCAVATION_ESCALATION_UNIT);
        registry.register(ATTACK_AMPLIFICATION_UNIT);
        registry.register(FARMING_UNIT);
        registry.register(SHEARING_UNIT);
        registry.register(SILK_TOUCH_UNIT);
        registry.register(FORTUNE_UNIT);
        registry.register(BLASTING_UNIT);
        registry.register(VEIN_MINING_UNIT);
        registry.register(TELEPORTATION_UNIT);
        registry.register(ELECTROLYTIC_BREATHING_UNIT);
        registry.register(INHALATION_PURIFICATION_UNIT);
        registry.register(VISION_ENHANCEMENT_UNIT);
        registry.register(NUTRITIONAL_INJECTION_UNIT);
        registry.register(DOSIMETER_UNIT);
        registry.register(GEIGER_UNIT);
        registry.register(JETPACK_UNIT);
        registry.register(CHARGE_DISTRIBUTION_UNIT);
        registry.register(GRAVITATIONAL_MODULATING_UNIT);
        registry.register(ELYTRA_UNIT);
        registry.register(LOCOMOTIVE_BOOSTING_UNIT);
        registry.register(GYROSCOPIC_STABILIZATION_UNIT);
        registry.register(HYDROSTATIC_REPULSOR_UNIT);
        registry.register(MOTORIZED_SERVO_UNIT);
        registry.register(HYDRAULIC_PROPULSION_UNIT);
        registry.register(MAGNETIC_ATTRACTION_UNIT);
        registry.register(FROST_WALKER_UNIT);
        registry.register(SOUL_SURFER_UNIT);
        bootstrapped = true;
    }

    private static ModuleType.Builder type(String name, ModuleTarget firstTarget, ModuleTarget... otherTargets) {
        return ModuleType.builder(new ResourceLocation(MekanismMekaSuit.MODID, name), firstTarget, otherTargets);
    }

    private static ModuleType.Builder type(String name, ModuleTarget[] targets) {
        ModuleTarget[] remaining = new ModuleTarget[targets.length - 1];
        System.arraycopy(targets, 1, remaining, 0, remaining.length);
        return type(name, targets[0], remaining);
    }
}
