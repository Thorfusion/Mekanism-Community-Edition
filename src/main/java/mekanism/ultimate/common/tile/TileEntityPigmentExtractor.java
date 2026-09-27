package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.TileNetworkList;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import mekanism.ultimate.common.recipe.UltimatePigmentRecipes;
import mekanism.ultimate.common.recipe.type.ItemToChemicalRecipeCE;
import mekanism.ultimate.common.util.ChemicalTransferUtilsCE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class TileEntityPigmentExtractor extends TileEntityPigmentMachine {

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_CONTAINER_SLOT = 1;
    public static final int ENERGY_SLOT = 2;
    public static final int BASE_TICKS = 100;
    private static final int[] SLOTS = {INPUT_SLOT, OUTPUT_CONTAINER_SLOT, ENERGY_SLOT};

    public final LongChemicalTank outputTank = new LongChemicalTank(TANK_CAPACITY);

    public TileEntityPigmentExtractor() {
        super("PigmentExtractor", "tile.PigmentExtractor.name", 40_000, 200, 3);
        inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            return;
        }
        ChargeUtils.discharge(ENERGY_SLOT, this);
        if (ChemicalTransferUtilsCE.fillItem(inventory.get(OUTPUT_CONTAINER_SLOT), outputTank, 256) > 0) {
            markDirty();
        }
        ItemToChemicalRecipeCE recipe = UltimatePigmentRecipes.findExtracting(inventory.get(INPUT_SLOT));
        boolean canProcess = recipe != null && recipe.getInput().hasRequiredAmount(inventory.get(INPUT_SLOT))
              && outputTank.insert(recipe.getOutput(), Action.SIMULATE) == recipe.getOutput().getAmount();
        if (canProcess && MekanismUtils.canFunction(this) && getEnergy() >= energyPerTick) {
            setEnergy(getEnergy() - energyPerTick);
            clientEnergyUsed = energyPerTick;
            setActive(true);
            if (++operatingTicks >= ticksRequired(BASE_TICKS)) {
                inventory.get(INPUT_SLOT).shrink(recipe.getInput().getAmount());
                outputTank.insert(recipe.getOutput(), Action.EXECUTE);
                operatingTicks = 0;
                markDirty();
            }
        } else {
            operatingTicks = 0;
            clientEnergyUsed = 0;
            setActive(false);
        }
        if (ChemicalTransferUtilsCE.emit(this, outputTank, 256) > 0) {
            markDirty();
        }
        prevEnergy = getEnergy();
    }

    @Override
    public int getChemicalTankCount(@Nullable EnumFacing side) {
        return 1;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank, @Nullable EnumFacing side) {
        if (tank != 0) throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
        return outputTank;
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        return false;
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        IChemicalStackCE stored = outputTank.getStack();
        return tank == 0 && stored != null && type != null
              && LongChemicalTank.isSameType(stored.getType(), type);
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (slot == INPUT_SLOT) return UltimatePigmentRecipes.findExtracting(stack) != null;
        if (slot == OUTPUT_CONTAINER_SLOT) return stack.hasCapability(
              mekanism.ultimate.common.capability.UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null);
        return slot == ENERGY_SLOT && ChargeUtils.canBeDischarged(stack);
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side) {
        return slot == OUTPUT_CONTAINER_SLOT || slot == ENERGY_SLOT && ChargeUtils.canBeOutputted(stack, false);
    }

    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(outputTank.getStored(), outputTank.getCapacity());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        operatingTicks = Math.max(0, data.getInteger("operatingTicks"));
        outputTank.readFromNBT(data.getCompoundTag("outputPigment"), UltimateChemicalRegistry.INSTANCE);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("operatingTicks", operatingTicks);
        NBTTagCompound tank = new NBTTagCompound();
        outputTank.writeToNBT(tank);
        data.setTag("outputPigment", tank);
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        super.handlePacketData(data);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            operatingTicks = data.readInt();
            clientEnergyUsed = data.readDouble();
            readTankSnapshot(data, outputTank);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(operatingTicks);
        data.add(clientEnergyUsed);
        addTankSnapshot(data, outputTank);
        return data;
    }
}
