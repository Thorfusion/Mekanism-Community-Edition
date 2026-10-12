package mekanism.mekasuit.common;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mekanism.common.Mekanism;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.item.IMekaSuitItem;
import mekanism.mekasuit.common.item.ItemMekaModule;
import mekanism.mekasuit.common.item.ItemMekaSuit;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import mekanism.mekasuit.common.item.ItemMekaTool;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;

/** Items owned and packaged by the MekaSuit module. */
public final class MekaSuitItems
{
    public static final ItemMekaTool MekaTool = new ItemMekaTool();
    public static final ItemMekaSuitArmor MekaSuitHelmet =
          new ItemMekaSuitArmor("mekasuit_helmet", "MekaSuitHelmet", 0);
    public static final ItemMekaSuitBodyarmor MekaSuitBodyarmor =
          new ItemMekaSuitBodyarmor("mekasuit_bodyarmor", "MekaSuitBodyarmor");
    public static final ItemMekaSuitArmor MekaSuitPants =
          new ItemMekaSuitArmor("mekasuit_pants", "MekaSuitPants", 2);
    public static final ItemMekaSuitArmor MekaSuitBoots =
          new ItemMekaSuitArmor("mekasuit_boots", "MekaSuitBoots", 3);
    public static final Item ModuleBase =
          new ItemMekaSuit("module_base", "ModuleBase");

    public static final ItemMekaModule EnergyUnit = module(
          MekaSuitModules.ENERGY_UNIT, EnumRarity.uncommon,
          "module_energy_unit", "ModuleEnergyUnit");
    public static final ItemMekaModule ColorModulationUnit = module(
          MekaSuitModules.COLOR_MODULATION_UNIT, EnumRarity.uncommon,
          "module_color_modulation_unit", "ModuleColorModulationUnit");
    public static final ItemMekaModule LaserDissipationUnit = module(
          MekaSuitModules.LASER_DISSIPATION_UNIT, EnumRarity.uncommon,
          "module_laser_dissipation_unit", "ModuleLaserDissipationUnit");
    public static final ItemMekaModule RadiationShieldingUnit = module(
          MekaSuitModules.RADIATION_SHIELDING_UNIT, EnumRarity.uncommon,
          "module_radiation_shielding_unit", "ModuleRadiationShieldingUnit");
    public static final ItemMekaModule ExcavationEscalationUnit = module(
          MekaSuitModules.EXCAVATION_ESCALATION_UNIT, EnumRarity.uncommon,
          "module_excavation_escalation_unit", "ModuleExcavationEscalationUnit");
    public static final ItemMekaModule AttackAmplificationUnit = module(
          MekaSuitModules.ATTACK_AMPLIFICATION_UNIT, EnumRarity.uncommon,
          "module_attack_amplification_unit", "ModuleAttackAmplificationUnit");
    public static final ItemMekaModule FarmingUnit = module(
          MekaSuitModules.FARMING_UNIT, EnumRarity.uncommon,
          "module_farming_unit", "ModuleFarmingUnit");
    public static final ItemMekaModule ShearingUnit = module(
          MekaSuitModules.SHEARING_UNIT, EnumRarity.uncommon,
          "module_shearing_unit", "ModuleShearingUnit");
    public static final ItemMekaModule SilkTouchUnit = module(
          MekaSuitModules.SILK_TOUCH_UNIT, EnumRarity.rare,
          "module_silk_touch_unit", "ModuleSilkTouchUnit");
    public static final ItemMekaModule FortuneUnit = module(
          MekaSuitModules.FORTUNE_UNIT, EnumRarity.rare,
          "module_fortune_unit", "ModuleFortuneUnit");
    public static final ItemMekaModule BlastingUnit = module(
          MekaSuitModules.BLASTING_UNIT, EnumRarity.rare,
          "module_blasting_unit", "ModuleBlastingUnit");
    public static final ItemMekaModule VeinMiningUnit = module(
          MekaSuitModules.VEIN_MINING_UNIT, EnumRarity.rare,
          "module_vein_mining_unit", "ModuleVeinMiningUnit");
    public static final ItemMekaModule TeleportationUnit = module(
          MekaSuitModules.TELEPORTATION_UNIT, EnumRarity.epic,
          "module_teleportation_unit", "ModuleTeleportationUnit");
    public static final ItemMekaModule ElectrolyticBreathingUnit = module(
          MekaSuitModules.ELECTROLYTIC_BREATHING_UNIT, EnumRarity.uncommon,
          "module_electrolytic_breathing_unit", "ModuleElectrolyticBreathingUnit");
    public static final ItemMekaModule InhalationPurificationUnit = module(
          MekaSuitModules.INHALATION_PURIFICATION_UNIT, EnumRarity.rare,
          "module_inhalation_purification_unit", "ModuleInhalationPurificationUnit");
    public static final ItemMekaModule VisionEnhancementUnit = module(
          MekaSuitModules.VISION_ENHANCEMENT_UNIT, EnumRarity.rare,
          "module_vision_enhancement_unit", "ModuleVisionEnhancementUnit");
    public static final ItemMekaModule NutritionalInjectionUnit = module(
          MekaSuitModules.NUTRITIONAL_INJECTION_UNIT, EnumRarity.rare,
          "module_nutritional_injection_unit", "ModuleNutritionalInjectionUnit");
    public static final ItemMekaModule DosimeterUnit = module(
          MekaSuitModules.DOSIMETER_UNIT, EnumRarity.uncommon,
          "module_dosimeter_unit", "ModuleDosimeterUnit");
    public static final ItemMekaModule GeigerUnit = module(
          MekaSuitModules.GEIGER_UNIT, EnumRarity.uncommon,
          "module_geiger_unit", "ModuleGeigerUnit");
    public static final ItemMekaModule JetpackUnit = module(
          MekaSuitModules.JETPACK_UNIT, EnumRarity.rare,
          "module_jetpack_unit", "ModuleJetpackUnit");
    public static final ItemMekaModule ChargeDistributionUnit = module(
          MekaSuitModules.CHARGE_DISTRIBUTION_UNIT, EnumRarity.rare,
          "module_charge_distribution_unit", "ModuleChargeDistributionUnit");
    public static final ItemMekaModule GravitationalModulatingUnit = module(
          MekaSuitModules.GRAVITATIONAL_MODULATING_UNIT, EnumRarity.epic,
          "module_gravitational_modulating_unit", "ModuleGravitationalModulatingUnit");
    public static final ItemMekaModule ElytraUnit = module(
          MekaSuitModules.ELYTRA_UNIT, EnumRarity.epic,
          "module_elytra_unit", "ModuleElytraUnit");
    public static final ItemMekaModule LocomotiveBoostingUnit = module(
          MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT, EnumRarity.rare,
          "module_locomotive_boosting_unit", "ModuleLocomotiveBoostingUnit");
    public static final ItemMekaModule GyroscopicStabilizationUnit = module(
          MekaSuitModules.GYROSCOPIC_STABILIZATION_UNIT, EnumRarity.rare,
          "module_gyroscopic_stabilization_unit", "ModuleGyroscopicStabilizationUnit");
    public static final ItemMekaModule HydrostaticRepulsorUnit = module(
          MekaSuitModules.HYDROSTATIC_REPULSOR_UNIT, EnumRarity.rare,
          "module_hydrostatic_repulsor_unit", "ModuleHydrostaticRepulsorUnit");
    public static final ItemMekaModule MotorizedServoUnit = module(
          MekaSuitModules.MOTORIZED_SERVO_UNIT, EnumRarity.rare,
          "module_motorized_servo_unit", "ModuleMotorizedServoUnit");
    public static final ItemMekaModule HydraulicPropulsionUnit = module(
          MekaSuitModules.HYDRAULIC_PROPULSION_UNIT, EnumRarity.rare,
          "module_hydraulic_propulsion_unit", "ModuleHydraulicPropulsionUnit");
    public static final ItemMekaModule MagneticAttractionUnit = module(
          MekaSuitModules.MAGNETIC_ATTRACTION_UNIT, EnumRarity.rare,
          "module_magnetic_attraction_unit", "ModuleMagneticAttractionUnit");
    public static final ItemMekaModule FrostWalkerUnit = module(
          MekaSuitModules.FROST_WALKER_UNIT, EnumRarity.rare,
          "module_frost_walker_unit", "ModuleFrostWalkerUnit");
    public static final ItemMekaModule SoulSurferUnit = module(
          MekaSuitModules.SOUL_SURFER_UNIT, EnumRarity.rare,
          "module_soul_surfer_unit", "ModuleSoulSurferUnit");

    private static final List<Item> ITEMS = Collections.unmodifiableList(
          Arrays.<Item>asList(MekaTool, MekaSuitHelmet, MekaSuitBodyarmor,
                MekaSuitPants, MekaSuitBoots, ModuleBase, EnergyUnit,
                ColorModulationUnit, LaserDissipationUnit,
                RadiationShieldingUnit, ExcavationEscalationUnit,
                AttackAmplificationUnit, FarmingUnit, ShearingUnit,
                SilkTouchUnit, FortuneUnit, BlastingUnit, VeinMiningUnit,
                TeleportationUnit, ElectrolyticBreathingUnit,
                InhalationPurificationUnit, VisionEnhancementUnit,
                NutritionalInjectionUnit, DosimeterUnit, GeigerUnit,
                JetpackUnit, ChargeDistributionUnit,
                GravitationalModulatingUnit, ElytraUnit,
                LocomotiveBoostingUnit, GyroscopicStabilizationUnit,
                HydrostaticRepulsorUnit, MotorizedServoUnit,
                HydraulicPropulsionUnit, MagneticAttractionUnit,
                FrostWalkerUnit, SoulSurferUnit));

    private MekaSuitItems()
    {
    }

    public static void register()
    {
        MekaSuitModules.bootstrap();
        for (Item item : ITEMS)
        {
            item.setCreativeTab(Mekanism.tabMekanism);
            GameRegistry.registerItem(item,
                  ((IMekaSuitItem)item).getRegistryName());
        }
    }

    public static List<Item> allRegistered()
    {
        return ITEMS;
    }

    public static ItemMekaModule getModuleItem(ModuleType type)
    {
        return ItemMekaModule.getFor(type);
    }

    private static ItemMekaModule module(ModuleType type, EnumRarity rarity,
          String registryName, String translationKey)
    {
        return new ItemMekaModule(type, rarity, registryName, translationKey);
    }
}
