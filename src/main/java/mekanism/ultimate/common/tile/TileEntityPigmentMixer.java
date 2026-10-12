package mekanism.ultimate.common.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import javax.annotation.Nullable;
import mekanism.common.Upgrade;
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
import mekanism.ultimate.common.recipe.type.ChemicalChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.util.ChemicalTransferUtilsCE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

/** Mixes two pigments symmetrically with speed-scaled operations per tick. */
public final class TileEntityPigmentMixer extends TileEntityPigmentMachine
{
    public static final int LEFT_CONTAINER_SLOT = 0;
    public static final int RIGHT_CONTAINER_SLOT = 1;
    public static final int OUTPUT_CONTAINER_SLOT = 2;
    public static final int ENERGY_SLOT = 3;
    public static final int UPGRADE_SLOT = 4;

    private static final int[] SLOTS = {
          LEFT_CONTAINER_SLOT, RIGHT_CONTAINER_SLOT,
          OUTPUT_CONTAINER_SLOT, ENERGY_SLOT
    };

    public final LongChemicalTank leftTank =
          new LongChemicalTank(TANK_CAPACITY);
    public final LongChemicalTank rightTank =
          new LongChemicalTank(TANK_CAPACITY);
    public final LongChemicalTank outputTank =
          new LongChemicalTank(2 * TANK_CAPACITY);

    public TileEntityPigmentMixer()
    {
        super("PigmentMixer", "tile.PigmentMixer.name",
              80_000D, 200D, UPGRADE_SLOT);
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
              inventory[LEFT_CONTAINER_SLOT], leftTank, 256,
              ChemicalKind.PIGMENT) > 0;
        changed |= ChemicalTransferUtilsCE.drainItem(
              inventory[RIGHT_CONTAINER_SLOT], rightTank, 256,
              ChemicalKind.PIGMENT) > 0;
        changed |= ChemicalTransferUtilsCE.fillItem(
              inventory[OUTPUT_CONTAINER_SLOT], outputTank, 256) > 0;

        int completed = 0;
        int maxOperations = 1 << upgradeComponent.getUpgrades(
              Upgrade.SPEED);
        while (completed < maxOperations && getEnergy() >= energyPerTick
              && MekanismUtils.canFunction(this))
        {
            ChemicalChemicalToChemicalRecipeCE recipe = getRecipe();
            if (!canOperate(recipe))
            {
                break;
            }
            operate(recipe);
            setEnergy(getEnergy() - energyPerTick);
            completed++;
            changed = true;
        }

        clientEnergyUsed = completed * energyPerTick;
        setActive(completed > 0);
        changed |= ChemicalTransferUtilsCE.emit(this, outputTank, 256) > 0;
        if (changed)
        {
            markDirty();
        }
        prevEnergy = getEnergy();
        updateComparator(outputTank);
    }

    @Nullable
    public ChemicalChemicalToChemicalRecipeCE getRecipe()
    {
        return UltimatePigmentRecipes.findMixing(leftTank.getStack(),
              rightTank.getStack());
    }

    public boolean canOperate(
          @Nullable ChemicalChemicalToChemicalRecipeCE recipe)
    {
        if (recipe == null || inputAmounts(recipe) == null)
        {
            return false;
        }
        long output = recipe.getOutput().getAmount();
        return outputTank.insert(recipe.getOutput(), Action.SIMULATE)
              == output;
    }

    void operate(ChemicalChemicalToChemicalRecipeCE recipe)
    {
        long[] amounts = inputAmounts(recipe);
        if (amounts == null || outputTank.insert(recipe.getOutput(),
              Action.SIMULATE) != recipe.getOutput().getAmount())
        {
            throw new IllegalStateException(
                  "Pigment mixing attempted without valid inputs");
        }
        IChemicalStackCE left = leftTank.extract(amounts[0],
              Action.EXECUTE);
        IChemicalStackCE right = rightTank.extract(amounts[1],
              Action.EXECUTE);
        long inserted = outputTank.insert(recipe.getOutput(),
              Action.EXECUTE);
        if (left == null || left.getAmount() != amounts[0]
              || right == null || right.getAmount() != amounts[1]
              || inserted != recipe.getOutput().getAmount())
        {
            throw new IllegalStateException(
                  "Pigment mixing commit diverged from simulation");
        }
        markDirty();
    }

    @Nullable
    private long[] inputAmounts(
          ChemicalChemicalToChemicalRecipeCE recipe)
    {
        IChemicalStackCE left = leftTank.getStack();
        IChemicalStackCE right = rightTank.getStack();
        if (left == null || right == null)
        {
            return null;
        }
        if (recipe.getLeftInput().hasRequiredAmount(left)
              && recipe.getRightInput().hasRequiredAmount(right))
        {
            return new long[] {recipe.getLeftInput().getAmount(),
                  recipe.getRightInput().getAmount()};
        }
        if (recipe.getRightInput().hasRequiredAmount(left)
              && recipe.getLeftInput().hasRequiredAmount(right))
        {
            return new long[] {recipe.getRightInput().getAmount(),
                  recipe.getLeftInput().getAmount()};
        }
        return null;
    }

    @Override
    public int getChemicalTankCount(@Nullable ForgeDirection side)
    {
        return 3;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank,
          @Nullable ForgeDirection side)
    {
        switch (tank)
        {
            case 0:
                return leftTank;
            case 1:
                return rightTank;
            case 2:
                return outputTank;
            default:
                throw new IndexOutOfBoundsException(
                      "Chemical tank index: " + tank);
        }
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        return (tank == 0 || tank == 1) && type != null
              && type.getKind() == ChemicalKind.PIGMENT
              && getChemicalTank(tank, side).insert(
                    new ChemicalStackCE(type, 1), Action.SIMULATE) > 0;
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        IChemicalStackCE stored = outputTank.getStack();
        return tank == 2 && type != null && stored != null
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
        if (slot >= LEFT_CONTAINER_SLOT && slot <= OUTPUT_CONTAINER_SLOT)
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
        return stack != null && (slot >= LEFT_CONTAINER_SLOT
              && slot <= OUTPUT_CONTAINER_SLOT || slot == ENERGY_SLOT
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
        readTank(data, "leftPigment", leftTank);
        readTank(data, "rightPigment", rightTank);
        readTank(data, "outputPigment", outputTank);
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        writeTank(data, "leftPigment", leftTank);
        writeTank(data, "rightPigment", rightTank);
        writeTank(data, "outputPigment", outputTank);
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            readTankSnapshot(data, leftTank);
            readTankSnapshot(data, rightTank);
            readTankSnapshot(data, outputTank);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        addTankSnapshot(data, leftTank);
        addTankSnapshot(data, rightTank);
        addTankSnapshot(data, outputTank);
        return data;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord,
              xCoord + 1, yCoord + 2, zCoord + 1);
    }
}
