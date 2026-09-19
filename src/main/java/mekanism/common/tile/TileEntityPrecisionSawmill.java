package mekanism.common.tile;

import java.util.Map;

import mekanism.api.MekanismConfig.usage;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.MekanismBlocks;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Upgrade;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.base.ITierUpgradeable;
import mekanism.common.block.BlockMachine.MachineType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.SawmillRecipe;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityPrecisionSawmill extends TileEntityChanceMachine<SawmillRecipe> implements ITierUpgradeable
{
	public TileEntityPrecisionSawmill()
	{
		super("sawmill", "PrecisionSawmill", MekanismUtils.getResource(ResourceType.GUI, "GuiBasicMachine.png"), usage.precisionSawmillUsage, 200, MachineType.PRECISION_SAWMILL.baseEnergy);
	}

	@Override
	public Map<ItemStackInput, SawmillRecipe> getRecipes()
	{
		return Recipe.PRECISION_SAWMILL.get();
	}

	@Override
	public boolean upgrade(BaseTier upgradeTier)
	{
		if(upgradeTier != BaseTier.BASIC || !RecipeType.SAWING.isEnabled())
		{
			return false;
		}

		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		worldObj.setBlock(xCoord, yCoord, zCoord, MekanismBlocks.MachineBlock, 5, 3);

		TileEntityFactory factory = (TileEntityFactory)worldObj.getTileEntity(xCoord, yCoord, zCoord);
		factory.facing = facing;
		factory.clientFacing = clientFacing;
		factory.ticker = ticker;
		factory.redstone = redstone;
		factory.redstoneLastTick = redstoneLastTick;
		factory.doAutoSync = doAutoSync;
		factory.electricityStored = electricityStored;
		factory.soundURL = soundURL;
		factory.progress[0] = operatingTicks;
		factory.clientActive = clientActive;
		factory.isActive = isActive;
		factory.updateDelay = updateDelay;
		factory.controlType = controlType;
		factory.prevEnergy = prevEnergy;
		factory.upgradeComponent.readFrom(upgradeComponent);
		factory.upgradeComponent.setUpgradeSlot(0);
		factory.ejectorComponent.readFrom(ejectorComponent);
		factory.recipeType = RecipeType.SAWING;
		factory.upgradeComponent.setSupported(Upgrade.GAS, false);
		factory.secondaryEnergyPerTick = factory.getSecondaryEnergyPerTick(RecipeType.SAWING);
		factory.updateOutputSlots();
		factory.ejectorComponent.setOutputData(TransmissionType.ITEM, factory.configComponent.getOutputs(TransmissionType.ITEM).get(2));
		factory.securityComponent.readFrom(securityComponent);

		for(TransmissionType transmission : configComponent.transmissions)
		{
			factory.configComponent.setConfig(transmission, configComponent.getConfig(transmission));
			factory.configComponent.setEjecting(transmission, configComponent.isEjecting(transmission));
		}

		factory.inventory[5] = inventory[0];
		factory.inventory[1] = inventory[1];
		factory.inventory[8] = inventory[2];
		factory.inventory[0] = inventory[3];
		factory.inventory[4] = inventory[4];

		for(Upgrade upgrade : factory.upgradeComponent.getSupportedTypes())
		{
			factory.recalculateUpgradables(upgrade);
		}

		factory.upgraded = true;
		factory.markDirty();
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getVolume()
	{
		return 0.7F*super.getVolume();
	}
}
