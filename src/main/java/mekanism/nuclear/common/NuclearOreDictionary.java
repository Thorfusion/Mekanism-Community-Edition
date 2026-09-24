package mekanism.nuclear.common;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/** Stable 1.12 interoperability names for Nuclear-owned materials. */
public final class NuclearOreDictionary {

    public static final String INGOT_URANIUM = "ingotUranium";
    public static final String DUST_URANIUM = "dustUranium";
    public static final String ORE_URANIUM = "oreUranium";
    public static final String GEM_FLUORITE = "gemFluorite";
    public static final String DUST_FLUORITE = "dustFluorite";
    public static final String ORE_FLUORITE = "oreFluorite";

    private NuclearOreDictionary() {
    }

    public static synchronized void register() {
        registerIfAbsent(INGOT_URANIUM, new ItemStack(NuclearItems.UraniumIngot));
        registerIfAbsent(DUST_URANIUM, new ItemStack(NuclearItems.UraniumDust));
        registerIfAbsent(GEM_FLUORITE, new ItemStack(NuclearItems.FluoriteGem));
        registerIfAbsent(DUST_FLUORITE, new ItemStack(NuclearItems.FluoriteDust));
    }

    private static void registerIfAbsent(String name, ItemStack stack) {
        for (ItemStack existing : OreDictionary.getOres(name, false)) {
            if (OreDictionary.itemMatches(existing, stack, false)) {
                return;
            }
        }
        OreDictionary.registerOre(name, stack);
    }
}
