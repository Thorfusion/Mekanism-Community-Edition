package defense.common;

import java.lang.ref.WeakReference;

import mekanism.common.Mekanism;
import mekanism.common.Tier.BaseTier;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import defense.common.item.ItemBlockMachine;

public final class DefenseUtils 
{
	public static ItemStack getLauncherBase(BaseTier tier)
	{
		ItemStack stack = new ItemStack(DefenseTechBlocks.blockMachine, 1, 0);
		ItemBlockMachine itemMachine = (ItemBlockMachine)stack.getItem();
		itemMachine.setBaseTier(stack, tier);
		
		return stack;
	}
	
	public static ItemStack getLauncherScreen(BaseTier tier)
	{
		ItemStack stack = new ItemStack(DefenseTechBlocks.blockMachine, 1, 1);
		ItemBlockMachine itemMachine = (ItemBlockMachine)stack.getItem();
		itemMachine.setBaseTier(stack, tier);
		
		return stack;
	}
	
	public static ItemStack getLauncherFrame(BaseTier tier)
	{
		ItemStack stack = new ItemStack(DefenseTechBlocks.blockMachine, 1, 2);
		ItemBlockMachine itemMachine = (ItemBlockMachine)stack.getItem();
		itemMachine.setBaseTier(stack, tier);
		
		return stack;
	}
	
	public static boolean canBreak(World world, Block block, double x, double y, double z)
	{
		if(world.isRemote)
		{
			return true;
		}
		
		WeakReference<EntityPlayer> playerRef = Mekanism.proxy.getDummyPlayer((WorldServer)world, x, y, z);
    	BlockEvent.BreakEvent evt = new BlockEvent.BreakEvent((int)x, (int)y, (int)z, world, block, world.getBlockMetadata((int)x, (int)y, (int)z), playerRef.get());
    	MinecraftForge.EVENT_BUS.post(evt);
    	
    	return !evt.isCanceled();
	}
}
