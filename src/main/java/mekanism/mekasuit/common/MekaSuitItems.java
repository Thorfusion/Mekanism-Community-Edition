package mekanism.mekasuit.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mekanism.common.item.ItemMekanism;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.item.ItemMekaModule;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import mekanism.mekasuit.common.item.ItemMekaTool;
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
    public static final ItemMekaModule EnergyUnit = init(
          new ItemMekaModule(MekaSuitModules.ENERGY_UNIT, EnumRarity.UNCOMMON), "module_energy_unit", "ModuleEnergyUnit");
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

    private static final List<Item> ITEMS = Collections.unmodifiableList(Arrays.asList(
          MekaTool, MekaSuitHelmet, MekaSuitBodyarmor, MekaSuitPants, MekaSuitBoots, ModuleBase, EnergyUnit,
          ExcavationEscalationUnit, AttackAmplificationUnit, FarmingUnit, ShearingUnit, SilkTouchUnit, FortuneUnit,
          BlastingUnit, VeinMiningUnit, TeleportationUnit, ElectrolyticBreathingUnit,
          InhalationPurificationUnit, VisionEnhancementUnit, NutritionalInjectionUnit, JetpackUnit,
          ChargeDistributionUnit));

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
