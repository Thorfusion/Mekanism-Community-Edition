package mekanism.mekasuit.common;

import cpw.mods.fml.common.registry.GameRegistry;
import mekanism.mekasuit.common.block.BlockModificationStation;
import mekanism.mekasuit.common.item.ItemBlockModificationStation;
import net.minecraft.block.Block;

/** Blocks owned and packaged by the MekaSuit module. */
public final class MekaSuitBlocks
{
    public static final Block ModificationStation =
          new BlockModificationStation();

    private MekaSuitBlocks()
    {
    }

    public static void register()
    {
        GameRegistry.registerBlock(ModificationStation,
              ItemBlockModificationStation.class,
              "modification_station");
    }
}
