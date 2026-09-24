package mekanism.mekasuit.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mekanism.common.item.ItemMekanism;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.item.ItemMekaModule;
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
    public static final ItemMekaSuitArmor MekaSuitBodyarmor = init(
          new ItemMekaSuitArmor(EntityEquipmentSlot.CHEST), "mekasuit_bodyarmor", "MekaSuitBodyarmor");
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

    private static final List<Item> ITEMS = Collections.unmodifiableList(Arrays.asList(
          MekaTool, MekaSuitHelmet, MekaSuitBodyarmor, MekaSuitPants, MekaSuitBoots, ModuleBase, EnergyUnit,
          ExcavationEscalationUnit, AttackAmplificationUnit, FarmingUnit, ShearingUnit, SilkTouchUnit, FortuneUnit));

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
