package defense.common.item;

import mekanism.common.Tier.BaseTier;
import mekanism.common.base.ITierItem;
import mekanism.common.tile.TileEntityBasicBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import defense.api.ITier;
import defense.common.block.BlockMachine;
import defense.common.block.BlockMachine.MachineData;

public class ItemBlockMachine extends ItemBlock implements ITierItem
{
    public ItemBlockMachine(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
    	MachineData type = MachineData.get(itemstack.getItemDamage());
    	
    	if(type == null)
    	{
    		return "error";
    	}
    	
    	String name = getUnlocalizedName() + "." + type.unlocalized;
    	
    	if(type.hasTier)
    	{
    		name += "." + getBaseTier(itemstack).ordinal();
    	}
    	
    	return name;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "machine";
    }

    @Override
	public BaseTier getBaseTier(ItemStack itemstack)
	{
		if(itemstack.stackTagCompound == null)
		{
			return BaseTier.BASIC;
		}

		return BaseTier.values()[itemstack.stackTagCompound.getInteger("tier")];
	}

	@Override
	public void setBaseTier(ItemStack itemstack, BaseTier tier)
	{
		if(itemstack.stackTagCompound == null)
		{
			itemstack.setTagCompound(new NBTTagCompound());
		}

		itemstack.stackTagCompound.setInteger("tier", tier.ordinal());
	}
    
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
    	int change = 3;
        int direction = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        
        switch(direction)
		{
			case 0: change = 2; break;
			case 1: change = 5; break;
			case 2: change = 3; break;
			case 3: change = 4; break;
		}
        
        boolean place = BlockMachine.canBePlacedAt(world, x, y, z, metadata, change);

        if(place && super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
        {
        	TileEntityBasicBlock tileEntity = (TileEntityBasicBlock)world.getTileEntity(x, y, z);
        	
        	if(tileEntity instanceof ITier)
        	{
        		((ITier)tileEntity).setTier(getBaseTier(stack).ordinal());
        	}
        	
            return true;
        }

        return false;
    }
}
