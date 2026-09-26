package mekanism.mekasuit.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mekanism.common.item.ItemMekanism;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.item.ItemMekaModule;
import mekanism.mekasuit.common.item.ItemHDPEElytra;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import mekanism.mekasuit.common.item.ItemMekaTool;
import mekanism.mekasuit.common.item.ItemArmoredFreeRunners;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

/** Items owned and packaged by the MekaSuit module. */
public final class MekaSuitItems {

    public static final ItemMekaTool MekaTool = init(new ItemMekaTool(), "meka_tool", "MekaTool");
    public static final ItemMekaSuitArmor MekaSuitHelmet = init(
          new ItemMekaSuitArmor(EntityEquipmentSlot.HEAD), "mekasuit_helmet", "MekaSuitHelmet");
    public static final ItemMekaSuitBodyarmor MekaSuitBodyarmor = init(
          new ItemMekaSuitBodyarmor(), "mekasuit_bodyarmor", "MekaSuitBodyarmor");
    public static final ItemMekaSuitArmor MekaSuitPants = init(
          new ItemMekaSuitArmor(EntityEquipmentSlot.LEGS), "mekasuit_pants", "MekaSuitPants");
    public static final ItemMekaSuitArmor MekaSuitBoots = init(
          new ItemMekaSuitArmor(EntityEquipmentSlot.FEET), "mekasuit_boots", "MekaSuitBoots");
    public static final Item ModuleBase = init(new ItemMekanism(), "module_base", "ModuleBase");
    public static final ItemHDPEElytra HDPEElytra = init(
          new ItemHDPEElytra(), "hdpe_elytra", "HDPEElytra");
    public static final ItemArmoredFreeRunners ArmoredFreeRunners = init(
          new ItemArmoredFreeRunners(), "armored_free_runners", "ArmoredFreeRunners");
    public static final ItemMekaModule EnergyUnit = init(
          new ItemMekaModule(MekaSuitModules.ENERGY_UNIT, EnumRarity.UNCOMMON), "module_energy_unit", "ModuleEnergyUnit");
    public static final ItemMekaModule ColorModulationUnit = init(
          new ItemMekaModule(MekaSuitModules.COLOR_MODULATION_UNIT, EnumRarity.UNCOMMON),
          "module_color_modulation_unit", "ModuleColorModulationUnit");
    public static final ItemMekaModule LaserDissipationUnit = init(
          new ItemMekaModule(MekaSuitModules.LASER_DISSIPATION_UNIT, EnumRarity.UNCOMMON),
          "module_laser_dissipation_unit", "ModuleLaserDissipationUnit");
    public static final ItemMekaModule RadiationShieldingUnit = init(
          new ItemMekaModule(MekaSuitModules.RADIATION_SHIELDING_UNIT, EnumRarity.UNCOMMON),
          "module_radiation_shielding_unit", "ModuleRadiationShieldingUnit");
    public static final ItemMekaModule ExcavationEscalationUnit = init(
          new ItemMekaModule(MekaSuitModules.EXCAVATION_ESCALATION_UNIT, EnumRarity.UNCOMMON),
          "module_excavation_escalation_unit", "ModuleExcavationEscalationUnit");
    public static final ItemMekaModule AttackAmplificationUnit = init(
          new ItemMekaModule(MekaSuitModules.ATTACK_AMPLIFICATION_UNIT, EnumRarity.UNCOMMON),
          "module_attack_amplification_unit", "ModuleAttackAmplificationUnit");
    public static final ItemMekaModule FarmingUnit = init(
          new ItemMekaModule(MekaSuitModules.FARMING_UNIT, EnumRarity.UNCOMMON),
          "module_farming_unit", "ModuleFarmingUnit");
    public static final ItemMekaModule ShearingUnit = init(
          new ItemMekaModule(MekaSuitModules.SHEARING_UNIT, EnumRarity.UNCOMMON),
          "module_shearing_unit", "ModuleShearingUnit");
    public static final ItemMekaModule SilkTouchUnit = init(
          new ItemMekaModule(MekaSuitModules.SILK_TOUCH_UNIT, EnumRarity.RARE),
          "module_silk_touch_unit", "ModuleSilkTouchUnit");
    public static final ItemMekaModule FortuneUnit = init(
          new ItemMekaModule(MekaSuitModules.FORTUNE_UNIT, EnumRarity.RARE),
          "module_fortune_unit", "ModuleFortuneUnit");
    public static final ItemMekaModule BlastingUnit = init(
          new ItemMekaModule(MekaSuitModules.BLASTING_UNIT, EnumRarity.RARE),
          "module_blasting_unit", "ModuleBlastingUnit");
    public static final ItemMekaModule VeinMiningUnit = init(
          new ItemMekaModule(MekaSuitModules.VEIN_MINING_UNIT, EnumRarity.RARE),
          "module_vein_mining_unit", "ModuleVeinMiningUnit");
    public static final ItemMekaModule TeleportationUnit = init(
          new ItemMekaModule(MekaSuitModules.TELEPORTATION_UNIT, EnumRarity.EPIC),
          "module_teleportation_unit", "ModuleTeleportationUnit");
    public static final ItemMekaModule ElectrolyticBreathingUnit = init(
          new ItemMekaModule(MekaSuitModules.ELECTROLYTIC_BREATHING_UNIT, EnumRarity.UNCOMMON),
          "module_electrolytic_breathing_unit", "ModuleElectrolyticBreathingUnit");
    public static final ItemMekaModule InhalationPurificationUnit = init(
          new ItemMekaModule(MekaSuitModules.INHALATION_PURIFICATION_UNIT, EnumRarity.RARE),
          "module_inhalation_purification_unit", "ModuleInhalationPurificationUnit");
    public static final ItemMekaModule VisionEnhancementUnit = init(
          new ItemMekaModule(MekaSuitModules.VISION_ENHANCEMENT_UNIT, EnumRarity.RARE),
          "module_vision_enhancement_unit", "ModuleVisionEnhancementUnit");
    public static final ItemMekaModule NutritionalInjectionUnit = init(
          new ItemMekaModule(MekaSuitModules.NUTRITIONAL_INJECTION_UNIT, EnumRarity.RARE),
          "module_nutritional_injection_unit", "ModuleNutritionalInjectionUnit");
    public static final ItemMekaModule JetpackUnit = init(
          new ItemMekaModule(MekaSuitModules.JETPACK_UNIT, EnumRarity.RARE),
          "module_jetpack_unit", "ModuleJetpackUnit");
    public static final ItemMekaModule ChargeDistributionUnit = init(
          new ItemMekaModule(MekaSuitModules.CHARGE_DISTRIBUTION_UNIT, EnumRarity.RARE),
          "module_charge_distribution_unit", "ModuleChargeDistributionUnit");
    public static final ItemMekaModule GravitationalModulatingUnit = init(
          new ItemMekaModule(MekaSuitModules.GRAVITATIONAL_MODULATING_UNIT, EnumRarity.EPIC),
          "module_gravitational_modulating_unit", "ModuleGravitationalModulatingUnit");
    public static final ItemMekaModule ElytraUnit = init(
          new ItemMekaModule(MekaSuitModules.ELYTRA_UNIT, EnumRarity.EPIC),
          "module_elytra_unit", "ModuleElytraUnit");
    public static final ItemMekaModule DosimeterUnit = init(
          new ItemMekaModule(MekaSuitModules.DOSIMETER_UNIT, EnumRarity.UNCOMMON),
          "module_dosimeter_unit", "ModuleDosimeterUnit");
    public static final ItemMekaModule GeigerUnit = init(
          new ItemMekaModule(MekaSuitModules.GEIGER_UNIT, EnumRarity.UNCOMMON),
          "module_geiger_unit", "ModuleGeigerUnit");
    public static final ItemMekaModule LocomotiveBoostingUnit = init(
          new ItemMekaModule(MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT, EnumRarity.RARE),
          "module_locomotive_boosting_unit", "ModuleLocomotiveBoostingUnit");
    public static final ItemMekaModule GyroscopicStabilizationUnit = init(
          new ItemMekaModule(MekaSuitModules.GYROSCOPIC_STABILIZATION_UNIT, EnumRarity.RARE),
          "module_gyroscopic_stabilization_unit", "ModuleGyroscopicStabilizationUnit");
    public static final ItemMekaModule HydrostaticRepulsorUnit = init(
          new ItemMekaModule(MekaSuitModules.HYDROSTATIC_REPULSOR_UNIT, EnumRarity.RARE),
          "module_hydrostatic_repulsor_unit", "ModuleHydrostaticRepulsorUnit");
    public static final ItemMekaModule MotorizedServoUnit = init(
          new ItemMekaModule(MekaSuitModules.MOTORIZED_SERVO_UNIT, EnumRarity.RARE),
          "module_motorized_servo_unit", "ModuleMotorizedServoUnit");
    public static final ItemMekaModule HydraulicPropulsionUnit = init(
          new ItemMekaModule(MekaSuitModules.HYDRAULIC_PROPULSION_UNIT, EnumRarity.RARE),
          "module_hydraulic_propulsion_unit", "ModuleHydraulicPropulsionUnit");
    public static final ItemMekaModule MagneticAttractionUnit = init(
          new ItemMekaModule(MekaSuitModules.MAGNETIC_ATTRACTION_UNIT, EnumRarity.RARE),
          "module_magnetic_attraction_unit", "ModuleMagneticAttractionUnit");
    public static final ItemMekaModule FrostWalkerUnit = init(
          new ItemMekaModule(MekaSuitModules.FROST_WALKER_UNIT, EnumRarity.RARE),
          "module_frost_walker_unit", "ModuleFrostWalkerUnit");
    public static final ItemMekaModule SoulSurferUnit = init(
          new ItemMekaModule(MekaSuitModules.SOUL_SURFER_UNIT, EnumRarity.RARE),
          "module_soul_surfer_unit", "ModuleSoulSurferUnit");

    private static final List<Item> ITEMS = Collections.unmodifiableList(Arrays.asList(
          MekaTool, MekaSuitHelmet, MekaSuitBodyarmor, MekaSuitPants, MekaSuitBoots, ModuleBase, HDPEElytra,
          ArmoredFreeRunners, EnergyUnit, ColorModulationUnit, LaserDissipationUnit, RadiationShieldingUnit,
          ExcavationEscalationUnit, AttackAmplificationUnit, FarmingUnit, ShearingUnit, SilkTouchUnit, FortuneUnit,
          BlastingUnit, VeinMiningUnit, TeleportationUnit, ElectrolyticBreathingUnit,
          InhalationPurificationUnit, VisionEnhancementUnit, NutritionalInjectionUnit, JetpackUnit,
          ChargeDistributionUnit, GravitationalModulatingUnit, ElytraUnit, DosimeterUnit, GeigerUnit,
          LocomotiveBoostingUnit, GyroscopicStabilizationUnit, HydrostaticRepulsorUnit, MotorizedServoUnit,
          HydraulicPropulsionUnit, MagneticAttractionUnit, FrostWalkerUnit, SoulSurferUnit));

    private MekaSuitItems() {
    }

    public static void registerItems(IForgeRegistry<Item> registry) {
        MekaSuitModules.bootstrap();
        for (Item item : ITEMS) {
            registry.register(item);
        }
    }

    public static List<Item> allRegistered() {
        return ITEMS;
    }

    public static ItemMekaModule getModuleItem(ModuleType type) {
        return ItemMekaModule.getFor(type);
    }

    private static <ITEM extends Item> ITEM init(ITEM item, String registryName, String translationKey) {
        item.setRegistryName(new ResourceLocation(MekanismMekaSuit.MODID, registryName));
        item.setTranslationKey(translationKey);
        return item;
    }
}
