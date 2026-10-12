package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import javax.annotation.Nullable;
import mekanism.common.base.IUpgradeItem;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.recipe.UltimatePigmentRecipes;
import mekanism.ultimate.common.recipe.type.ItemToChemicalRecipeCE;
import mekanism.ultimate.common.util.ChemicalTransferUtilsCE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/** Extracts a registered pigment from dyes and colored blocks. */
public final class TileEntityPigmentExtractor
      extends TileEntityPigmentMachine
{
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_CONTAINER_SLOT = 1;
    public static final int ENERGY_SLOT = 2;
    public static final int UPGRADE_SLOT = 3;
    public static final int BASE_TICKS = 100;

    private static final int[] SLOTS = {
          INPUT_SLOT, OUTPUT_CONTAINER_SLOT, ENERGY_SLOT
    };

    public final LongChemicalTank outputTank =
          new LongChemicalTank(TANK_CAPACITY);

    public TileEntityPigmentExtractor()
    {
        super("PigmentExtractor", "tile.PigmentExtractor.name",
              40_000D, 200D, UPGRADE_SLOT);
        inventory = new ItemStack[4];
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (worldObj.isRemote)
        {
            return;
        }

        ChargeUtils.discharge(ENERGY_SLOT, this);
        boolean changed = ChemicalTransferUtilsCE.fillItem(
              inventory[OUTPUT_CONTAINER_SLOT], outputTank, 256) > 0;
        ItemToChemicalRecipeCE recipe = getRecipe();
        boolean canProcess = canOperate(recipe);
        if (canProcess && MekanismUtils.canFunction(this)
              && getEnergy() >= energyPerTick)
        {
            setEnergy(getEnergy() - energyPerTick);
            clientEnergyUsed = energyPerTick;
            setActive(true);
            if (++operatingTicks >= ticksRequired(BASE_TICKS))
            {
                operate(recipe);
                operatingTicks = 0;
                changed = true;
            }
        }
        else
        {
            operatingTicks = 0;
            clientEnergyUsed = 0;
            setActive(false);
        }

        changed |= ChemicalTransferUtilsCE.emit(this, outputTank, 256) > 0;
        if (changed)
        {
            markDirty();
        }
        prevEnergy = getEnergy();
        updateComparator(outputTank);
    }

    @Nullable
    public ItemToChemicalRecipeCE getRecipe()
    {
        return UltimatePigmentRecipes.findExtracting(inventory[INPUT_SLOT]);
    }

    public boolean canOperate(@Nullable ItemToChemicalRecipeCE recipe)
    {
        if (recipe == null || !recipe.getInput().hasRequiredAmount(
              inventory[INPUT_SLOT]))
        {
            return false;
        }
        long output = recipe.getOutput().getAmount();
        return outputTank.insert(recipe.getOutput(), Action.SIMULATE)
              == output;
    }

    void operate(ItemToChemicalRecipeCE recipe)
    {
        if (!canOperate(recipe))
        {
            throw new IllegalStateException(
                  "Pigment extraction attempted without valid inputs");
        }
        ItemStack input = inventory[INPUT_SLOT];
        input.stackSize -= recipe.getInput().getAmount();
        if (input.stackSize <= 0)
        {
            inventory[INPUT_SLOT] = null;
        }
        long inserted = outputTank.insert(recipe.getOutput(),
              Action.EXECUTE);
        if (inserted != recipe.getOutput().getAmount())
        {
            throw new IllegalStateException(
                  "Pigment extraction commit diverged from simulation");
        }
        markDirty();
    }

    @Override
    public int getChemicalTankCount(@Nullable ForgeDirection side)
    {
        return 1;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank,
          @Nullable ForgeDirection side)
    {
        if (tank != 0)
        {
            throw new IndexOutOfBoundsException(
                  "Chemical tank index: " + tank);
        }
        return outputTank;
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        return false;
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        IChemicalStackCE stored = outputTank.getStack();
        return tank == 0 && type != null && stored != null
              && LongChemicalTank.isSameType(stored.getType(), type);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return SLOTS.clone();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }
        if (slot == INPUT_SLOT)
        {
            return UltimatePigmentRecipes.findExtracting(stack) != null;
        }
        if (slot == OUTPUT_CONTAINER_SLOT)
        {
            return ChemicalTransferUtilsCE.itemHandler(stack) != null;
        }
        if (slot == ENERGY_SLOT)
        {
            return ChargeUtils.canBeDischarged(stack);
        }
        if (slot == UPGRADE_SLOT
              && stack.getItem() instanceof IUpgradeItem)
        {
            return upgradeComponent.supports(((IUpgradeItem)stack.getItem())
                  .getUpgradeType(stack));
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return stack != null && (slot == OUTPUT_CONTAINER_SLOT
              || slot == ENERGY_SLOT
                    && ChargeUtils.canBeOutputted(stack, false));
    }

    public int getRedstoneLevel()
    {
        return redstoneLevel(outputTank);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        readTank(data, "outputPigment", outputTank);
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        writeTank(data, "outputPigment", outputTank);
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            readTankSnapshot(data, outputTank);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        addTankSnapshot(data, outputTank);
        return data;
    }
}
