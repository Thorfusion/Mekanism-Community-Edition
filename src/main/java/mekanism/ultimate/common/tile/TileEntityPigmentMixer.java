package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.TileNetworkList;
import mekanism.common.Upgrade;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import mekanism.ultimate.common.recipe.UltimatePigmentRecipes;
import mekanism.ultimate.common.recipe.type.ChemicalChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.util.ChemicalTransferUtilsCE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class TileEntityPigmentMixer extends TileEntityPigmentMachine {

    public static final int LEFT_CONTAINER_SLOT = 0;
    public static final int RIGHT_CONTAINER_SLOT = 1;
    public static final int OUTPUT_CONTAINER_SLOT = 2;
    public static final int ENERGY_SLOT = 3;
    private static final int[] SLOTS = {LEFT_CONTAINER_SLOT, RIGHT_CONTAINER_SLOT,
          OUTPUT_CONTAINER_SLOT, ENERGY_SLOT};

    public final LongChemicalTank leftTank = new LongChemicalTank(TANK_CAPACITY);
    public final LongChemicalTank rightTank = new LongChemicalTank(TANK_CAPACITY);
    public final LongChemicalTank outputTank = new LongChemicalTank(2 * TANK_CAPACITY);

    public TileEntityPigmentMixer() {
        super("PigmentMixer", "tile.PigmentMixer.name", 80_000, 200, 4);
        inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) return;
        ChargeUtils.discharge(ENERGY_SLOT, this);
        boolean changed = ChemicalTransferUtilsCE.drainItem(inventory.get(LEFT_CONTAINER_SLOT), leftTank,
              256, ChemicalKind.PIGMENT) > 0;
        changed |= ChemicalTransferUtilsCE.drainItem(inventory.get(RIGHT_CONTAINER_SLOT), rightTank,
              256, ChemicalKind.PIGMENT) > 0;
        changed |= ChemicalTransferUtilsCE.fillItem(inventory.get(OUTPUT_CONTAINER_SLOT), outputTank, 256) > 0;

        int completed = 0;
        int maxOperations = 1 << upgradeComponent.getUpgrades(Upgrade.SPEED);
        while (completed < maxOperations && getEnergy() >= energyPerTick && MekanismUtils.canFunction(this)) {
            ChemicalChemicalToChemicalRecipeCE recipe = UltimatePigmentRecipes.findMixing(
                  leftTank.getStack(), rightTank.getStack());
            if (recipe == null || outputTank.insert(recipe.getOutput(), Action.SIMULATE)
                  != recipe.getOutput().getAmount()) {
                break;
            }
            boolean normal = recipe.getLeftInput().hasRequiredAmount(leftTank.getStack())
                  && recipe.getRightInput().hasRequiredAmount(rightTank.getStack());
            long leftAmount = normal ? recipe.getLeftInput().getAmount() : recipe.getRightInput().getAmount();
            long rightAmount = normal ? recipe.getRightInput().getAmount() : recipe.getLeftInput().getAmount();
            IChemicalStackCE left = leftTank.extract(leftAmount, Action.SIMULATE);
            IChemicalStackCE right = rightTank.extract(rightAmount, Action.SIMULATE);
            if (left == null || left.getAmount() != leftAmount || right == null || right.getAmount() != rightAmount) {
                break;
            }
            leftTank.extract(leftAmount, Action.EXECUTE);
            rightTank.extract(rightAmount, Action.EXECUTE);
            outputTank.insert(recipe.getOutput(), Action.EXECUTE);
            setEnergy(getEnergy() - energyPerTick);
            completed++;
            changed = true;
        }
        clientEnergyUsed = completed * energyPerTick;
        setActive(completed > 0);
        changed |= ChemicalTransferUtilsCE.emit(this, outputTank, 256) > 0;
        if (changed) markDirty();
        prevEnergy = getEnergy();
    }

    @Override
    public int getChemicalTankCount(@Nullable EnumFacing side) {
        return 3;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank, @Nullable EnumFacing side) {
        if (tank == 0) return leftTank;
        if (tank == 1) return rightTank;
        if (tank == 2) return outputTank;
        throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        return (tank == 0 || tank == 1) && type != null && type.getKind() == ChemicalKind.PIGMENT
              && getChemicalTank(tank, side).insert(
                    new mekanism.ultimate.common.content.chemical.ChemicalStackCE(type, 1), Action.SIMULATE) > 0;
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        IChemicalStackCE stored = outputTank.getStack();
        return tank == 2 && stored != null && type != null
              && LongChemicalTank.isSameType(stored.getType(), type);
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (slot >= LEFT_CONTAINER_SLOT && slot <= OUTPUT_CONTAINER_SLOT) {
            return stack.hasCapability(mekanism.ultimate.common.capability.UltimateChemicalCapabilities
                  .CHEMICAL_HANDLER_CAPABILITY, null);
        }
        return slot == ENERGY_SLOT && ChargeUtils.canBeDischarged(stack);
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side) {
        return slot <= OUTPUT_CONTAINER_SLOT || slot == ENERGY_SLOT && ChargeUtils.canBeOutputted(stack, false);
    }

    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(outputTank.getStored(), outputTank.getCapacity());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        readTank(data, "leftPigment", leftTank);
        readTank(data, "rightPigment", rightTank);
        readTank(data, "outputPigment", outputTank);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        writeTank(data, "leftPigment", leftTank);
        writeTank(data, "rightPigment", rightTank);
        writeTank(data, "outputPigment", outputTank);
        return data;
    }

    private static void readTank(NBTTagCompound data, String key, LongChemicalTank tank) {
        tank.readFromNBT(data.getCompoundTag(key), UltimateChemicalRegistry.INSTANCE);
    }

    private static void writeTank(NBTTagCompound data, String key, LongChemicalTank tank) {
        NBTTagCompound tag = new NBTTagCompound();
        tank.writeToNBT(tag);
        data.setTag(key, tag);
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        super.handlePacketData(data);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            clientEnergyUsed = data.readDouble();
            readTankSnapshot(data, leftTank);
            readTankSnapshot(data, rightTank);
            readTankSnapshot(data, outputTank);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(clientEnergyUsed);
        addTankSnapshot(data, leftTank);
        addTankSnapshot(data, rightTank);
        addTankSnapshot(data, outputTank);
        return data;
    }
}
