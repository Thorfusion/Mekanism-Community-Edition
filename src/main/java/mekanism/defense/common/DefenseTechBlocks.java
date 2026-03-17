package defense.common;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import defense.common.block.BlockExplosive;
import defense.common.block.BlockMachine;
import defense.common.item.ItemBlockExplosive;
import defense.common.item.ItemBlockMachine;

@ObjectHolder(Reference.NAME)
public class DefenseTechBlocks 
{
    public static Block blockExplosive = new BlockExplosive();
    public static Block blockMachine = new BlockMachine();
    
    public static Block blockRadioactive;
    
	public static void register()
	{
        GameRegistry.registerBlock(blockExplosive, ItemBlockExplosive.class, "explosives");
        GameRegistry.registerBlock(blockMachine, ItemBlockMachine.class, "machine");
        
        /** Check for existence of radioactive Blocks. If it does not exist, then create it. */
        if(OreDictionary.getOres("blockRadioactive").size() > 0)
        {
            blockRadioactive = Block.getBlockFromItem(OreDictionary.getOres("blockRadioactive").get(0).getItem());
            DefenseTech.LOGGER.fine("Detected radioative block from another mod, utilizing it.");
        }
        else {
            blockRadioactive = Blocks.mycelium;
        }
	}
}
