package mekanism.api.lasers;

import net.minecraft.item.ItemStack;

/**
 * Armor-side laser protection contract. Percentages from equipped pieces are
 * accumulated by the Core laser path and clamped to one.
 */
public interface ILaserDissipation {

    double getDissipationPercent(ItemStack stack);

    double getRefractionPercent(ItemStack stack);
}
