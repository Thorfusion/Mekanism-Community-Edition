package mekanism.common.content.tank;

import mekanism.common.multiblock.MultiblockCache;
import mekanism.common.util.FluidContainerUtils.ContainerEditMode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;

public class TankCache extends MultiblockCache<SynchronizedTankData>
{	
	public ItemStack[] inventory = new ItemStack[2];
	
	public FluidStack fluid;

	public DynamicTankChemicalStack chemical;
	
	public ContainerEditMode editMode = ContainerEditMode.BOTH;
	
	@Override
	public void apply(SynchronizedTankData data)
	{
		data.inventory = inventory;
		data.fluidStored = fluid == null ? null : fluid.copy();
		data.chemicalStored = data.fluidStored == null && chemical != null ? chemical.copy() : null;
		data.editMode = editMode;
	}
	
	@Override
	public void sync(SynchronizedTankData data)
	{
		inventory = data.inventory;
		fluid = data.fluidStored == null ? null : data.fluidStored.copy();
		chemical = data.fluidStored == null && data.chemicalStored != null ? data.chemicalStored.copy() : null;
		editMode = data.editMode;
	}
	
	@Override
	public void load(NBTTagCompound nbtTags)
	{
		fluid = null;
		chemical = null;
		int mode = nbtTags.getInteger("editMode");
		editMode = mode >= 0 && mode < ContainerEditMode.values().length ? ContainerEditMode.values()[mode] : ContainerEditMode.BOTH;
		
		NBTTagList tagList = nbtTags.getTagList("Items", NBT.TAG_COMPOUND);
		inventory = new ItemStack[2];

		for(int tagCount = 0; tagCount < tagList.tagCount(); tagCount++)
		{
			NBTTagCompound tagCompound = (NBTTagCompound)tagList.getCompoundTagAt(tagCount);
			byte slotID = tagCompound.getByte("Slot");

			if(slotID >= 0 && slotID < 2)
			{
				inventory[slotID] = ItemStack.loadItemStackFromNBT(tagCompound);
			}
		}
		
		if(nbtTags.hasKey("cachedFluid"))
		{
			fluid = FluidStack.loadFluidStackFromNBT(nbtTags.getCompoundTag("cachedFluid"));
		}

		if(fluid == null && nbtTags.hasKey("cachedChemical", NBT.TAG_COMPOUND))
		{
			chemical = DynamicTankChemicalStack.read(nbtTags.getCompoundTag("cachedChemical"));
		}
	}
	
	@Override
	public void save(NBTTagCompound nbtTags)
	{
		nbtTags.setInteger("editMode", editMode.ordinal());
		
		NBTTagList tagList = new NBTTagList();

		for(int slotCount = 0; slotCount < 2; slotCount++)
		{
			if(inventory[slotCount] != null)
			{
				NBTTagCompound tagCompound = new NBTTagCompound();
				tagCompound.setByte("Slot", (byte)slotCount);
				inventory[slotCount].writeToNBT(tagCompound);
				tagList.appendTag(tagCompound);
			}
		}

		nbtTags.setTag("Items", tagList);
		nbtTags.removeTag("cachedFluid");
		nbtTags.removeTag("cachedChemical");
		
		if(fluid != null)
		{
			nbtTags.setTag("cachedFluid", fluid.writeToNBT(new NBTTagCompound()));
		}
		else if(chemical != null)
		{
			nbtTags.setTag("cachedChemical", chemical.write(new NBTTagCompound()));
		}
	}
}
