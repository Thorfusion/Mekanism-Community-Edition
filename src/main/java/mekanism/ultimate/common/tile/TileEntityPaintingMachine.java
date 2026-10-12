package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import javax.annotation.Nullable;
import mekanism.common.base.IUpgradeItem;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.recipe.UltimatePigmentRecipes;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import mekanism.ultimate.common.util.ChemicalTransferUtilsCE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/** Consumes an item and pigment to produce the matching colored item. */
public final class TileEntityPaintingMachine extends TileEntityPigmentMachine
{
    public static final int INPUT_SLOT = 0;
    public static final int PIGMENT_CONTAINER_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int ENERGY_SLOT = 3;
    public static final int UPGRADE_SLOT = 4;
    public static final int BASE_TICKS = 200;

    private static final int[] SLOTS = {
          INPUT_SLOT, PIGMENT_CONTAINER_SLOT, OUTPUT_SLOT, ENERGY_SLOT
    };

    public final LongChemicalTank pigmentTank =
          new LongChemicalTank(15_000L);

    public TileEntityPaintingMachine()
    {
        super("PaintingMachine", "tile.PaintingMachine.name",
              40_000D, 100D, UPGRADE_SLOT);
        inventory = new ItemStack[5];
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
        boolean changed = ChemicalTransferUtilsCE.drainItem(
              inventory[PIGMENT_CONTAINER_SLOT], pigmentTank, 256,
              ChemicalKind.PIGMENT) > 0;
        ItemChemicalToItemRecipeCE recipe = getRecipe();
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
        if (changed)
        {
            markDirty();
        }
        prevEnergy = getEnergy();
        updateComparator(pigmentTank);
    }

    @Nullable
    public ItemChemicalToItemRecipeCE getRecipe()
    {
        return UltimatePigmentRecipes.findPainting(inventory[INPUT_SLOT],
              pigmentTank.getStack());
    }

    public boolean canOperate(@Nullable ItemChemicalToItemRecipeCE recipe)
    {
        return recipe != null && recipe.getItemInput().hasRequiredAmount(
              inventory[INPUT_SLOT])
              && recipe.getChemicalInput().hasRequiredAmount(
                    pigmentTank.getStack())
              && canOutput(recipe.getOutput());
    }

    void operate(ItemChemicalToItemRecipeCE recipe)
    {
        if (!canOperate(recipe))
        {
            throw new IllegalStateException(
                  "Painting attempted without valid inputs");
        }
        long pigment = recipe.getChemicalInput().getAmount();
        IChemicalStackCE extracted = pigmentTank.extract(pigment,
              Action.EXECUTE);
        if (extracted == null || extracted.getAmount() != pigment)
        {
            throw new IllegalStateException(
                  "Painting chemical commit diverged from validation");
        }

        ItemStack input = inventory[INPUT_SLOT];
        input.stackSize -= recipe.getItemInput().getAmount();
        if (input.stackSize <= 0)
        {
            inventory[INPUT_SLOT] = null;
        }
        putOutput(recipe.getOutput());
        markDirty();
    }

    private boolean canOutput(ItemStack result)
    {
        ItemStack output = inventory[OUTPUT_SLOT];
        return output == null || output.isItemEqual(result)
              && ItemStack.areItemStackTagsEqual(output, result)
              && output.stackSize + result.stackSize <= Math.min(
                    output.getMaxStackSize(), getInventoryStackLimit());
    }

    private void putOutput(ItemStack result)
    {
        if (inventory[OUTPUT_SLOT] == null)
        {
            inventory[OUTPUT_SLOT] = result.copy();
        }
        else
        {
            inventory[OUTPUT_SLOT].stackSize += result.stackSize;
        }
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
        return pigmentTank;
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        return tank == 0 && type != null
              && type.getKind() == ChemicalKind.PIGMENT
              && pigmentTank.insert(new ChemicalStackCE(type, 1),
                    Action.SIMULATE) > 0;
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        return false;
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
            return UltimatePigmentRecipes.containsPaintingInput(stack);
        }
        if (slot == PIGMENT_CONTAINER_SLOT)
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
        return stack != null && (slot == OUTPUT_SLOT
              || slot == PIGMENT_CONTAINER_SLOT || slot == ENERGY_SLOT
                    && ChargeUtils.canBeOutputted(stack, false));
    }

    public int getRedstoneLevel()
    {
        return redstoneLevel(pigmentTank);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        readTank(data, "pigment", pigmentTank);
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        writeTank(data, "pigment", pigmentTank);
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            readTankSnapshot(data, pigmentTank);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        addTankSnapshot(data, pigmentTank);
        return data;
    }
}
