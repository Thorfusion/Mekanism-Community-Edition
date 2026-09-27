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
import mekanism.ultimate.api.recipe.input.ItemChemicalInputCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import mekanism.ultimate.common.recipe.UltimatePigmentRecipes;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import mekanism.ultimate.common.util.ChemicalTransferUtilsCE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class TileEntityPaintingMachine extends TileEntityPigmentMachine {

    public static final int INPUT_SLOT = 0;
    public static final int PIGMENT_CONTAINER_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int ENERGY_SLOT = 3;
    public static final int BASE_TICKS = 200;
    private static final int[] SLOTS = {INPUT_SLOT, PIGMENT_CONTAINER_SLOT, OUTPUT_SLOT, ENERGY_SLOT};

    public final LongChemicalTank pigmentTank = new LongChemicalTank(15_000);

    public TileEntityPaintingMachine() {
        super("PaintingMachine", "tile.PaintingMachine.name", 40_000, 100, 4);
        inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) return;
        ChargeUtils.discharge(ENERGY_SLOT, this);
        if (ChemicalTransferUtilsCE.drainItem(inventory.get(PIGMENT_CONTAINER_SLOT), pigmentTank,
              256, ChemicalKind.PIGMENT) > 0) markDirty();

        ItemChemicalToItemRecipeCE recipe = UltimatePigmentRecipes.findPainting(
              inventory.get(INPUT_SLOT), pigmentTank.getStack());
        boolean outputFits = recipe != null && canOutput(recipe.getOutput());
        boolean canProcess = outputFits && recipe.getItemInput().hasRequiredAmount(inventory.get(INPUT_SLOT))
              && recipe.getChemicalInput().hasRequiredAmount(pigmentTank.getStack());
        if (canProcess && MekanismUtils.canFunction(this) && getEnergy() >= energyPerTick) {
            setEnergy(getEnergy() - energyPerTick);
            clientEnergyUsed = energyPerTick;
            setActive(true);
            if (++operatingTicks >= ticksRequired(BASE_TICKS)) {
                inventory.get(INPUT_SLOT).shrink(recipe.getItemInput().getAmount());
                pigmentTank.extract(recipe.getChemicalInput().getAmount(), Action.EXECUTE);
                putOutput(recipe.getOutput());
                operatingTicks = 0;
                markDirty();
            }
        } else {
            operatingTicks = 0;
            clientEnergyUsed = 0;
            setActive(false);
        }
        prevEnergy = getEnergy();
    }

    private boolean canOutput(ItemStack result) {
        ItemStack output = inventory.get(OUTPUT_SLOT);
        return output.isEmpty() || ItemStack.areItemsEqual(output, result)
              && ItemStack.areItemStackTagsEqual(output, result)
              && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private void putOutput(ItemStack result) {
        if (inventory.get(OUTPUT_SLOT).isEmpty()) inventory.set(OUTPUT_SLOT, result.copy());
        else inventory.get(OUTPUT_SLOT).grow(result.getCount());
    }

    @Override
    public int getChemicalTankCount(@Nullable EnumFacing side) {
        return 1;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank, @Nullable EnumFacing side) {
        if (tank != 0) throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
        return pigmentTank;
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        return tank == 0 && type != null && type.getKind() == ChemicalKind.PIGMENT
              && pigmentTank.insert(new ChemicalStackCE(type, 1), Action.SIMULATE) > 0;
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        return false;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (slot == INPUT_SLOT) return UltimatePigmentRecipes.containsPaintingInput(stack);
        if (slot == PIGMENT_CONTAINER_SLOT) return stack.hasCapability(
              mekanism.ultimate.common.capability.UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null);
        return slot == ENERGY_SLOT && ChargeUtils.canBeDischarged(stack);
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side) {
        return slot == OUTPUT_SLOT || slot == PIGMENT_CONTAINER_SLOT
              || slot == ENERGY_SLOT && ChargeUtils.canBeOutputted(stack, false);
    }

    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(pigmentTank.getStored(), pigmentTank.getCapacity());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        operatingTicks = Math.max(0, data.getInteger("operatingTicks"));
        pigmentTank.readFromNBT(data.getCompoundTag("pigment"), UltimateChemicalRegistry.INSTANCE);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("operatingTicks", operatingTicks);
        NBTTagCompound tank = new NBTTagCompound();
        pigmentTank.writeToNBT(tank);
        data.setTag("pigment", tank);
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        super.handlePacketData(data);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            operatingTicks = data.readInt();
            clientEnergyUsed = data.readDouble();
            readTankSnapshot(data, pigmentTank);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(operatingTicks);
        data.add(clientEnergyUsed);
        addTankSnapshot(data, pigmentTank);
        return data;
    }
}
