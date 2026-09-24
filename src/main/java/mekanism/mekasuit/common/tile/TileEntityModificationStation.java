package mekanism.mekasuit.common.tile;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import mekanism.api.TileNetworkList;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.prefab.TileEntityElectricBlock;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.ModificationStationOperations;
import mekanism.mekasuit.common.item.ItemMekaModule;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;

/** Energy-backed installer for MekaSuit units. */
public final class TileEntityModificationStation extends TileEntityElectricBlock implements ISecurityTile {

    public static final int MODULE_SLOT = 0;
    public static final int CONTAINER_SLOT = 1;
    public static final int ENERGY_SLOT = 2;
    public static final int TICKS_REQUIRED = 10;

    private static final int[] AUTOMATION_SLOTS = {ENERGY_SLOT};

    private final TileComponentSecurity securityComponent = new TileComponentSecurity(this);
    private int operatingTicks;
    public double clientEnergyUsed;

    public TileEntityModificationStation() {
        super("ModificationStation", MekaSuitConfig.modificationStationCapacity);
        inventory = NonNullList.withSize(3, ItemStack.EMPTY);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            return;
        }
        ChargeUtils.discharge(ENERGY_SLOT, this);
        ItemStack moduleStack = inventory.get(MODULE_SLOT);
        ItemStack host = inventory.get(CONTAINER_SLOT);
        boolean canInstall = ModificationStationOperations.getInstallableCount(host, moduleStack) > 0;
        if (canInstall && MekanismUtils.canFunction(this) && getEnergy() >= MekaSuitConfig.modificationStationUsage) {
            setEnergy(getEnergy() - MekaSuitConfig.modificationStationUsage);
            clientEnergyUsed = MekaSuitConfig.modificationStationUsage;
            if (++operatingTicks >= TICKS_REQUIRED) {
                int installed = ModificationStationOperations.install(host, moduleStack);
                if (installed > 0) {
                    moduleStack.shrink(installed);
                    if (moduleStack.isEmpty()) {
                        inventory.set(MODULE_SLOT, ItemStack.EMPTY);
                    }
                    markDirty();
                }
                operatingTicks = 0;
            }
        } else {
            operatingTicks = 0;
            clientEnergyUsed = 0;
        }
    }

    public ItemStack getContainerStack() {
        return inventory.get(CONTAINER_SLOT);
    }

    public double getScaledProgress() {
        return (double) operatingTicks / TICKS_REQUIRED;
    }

    public int getOperatingTicks() {
        return operatingTicks;
    }

    public int getRedstoneLevel() {
        return Container.calcRedstoneFromInventory(this);
    }

    @Nonnull
    @Override
    public String getName() {
        return LangUtils.localize("tile.ModificationStation.name");
    }

    @Override
    public boolean sideIsConsumer(EnumFacing side) {
        return side == null || side == facing.getOpposite();
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing direction) {
        return direction.getAxis().isHorizontal();
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return AUTOMATION_SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (slot == MODULE_SLOT) {
            return stack.getItem() instanceof ItemMekaModule;
        } else if (slot == CONTAINER_SLOT) {
            return stack.getItem() instanceof IModuleContainerItem;
        } else if (slot == ENERGY_SLOT) {
            return ChargeUtils.canBeDischarged(stack);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side) {
        return slot == ENERGY_SLOT && ChargeUtils.canBeOutputted(stack, false);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        operatingTicks = Math.max(0, Math.min(TICKS_REQUIRED - 1, data.getInteger("operatingTicks")));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("operatingTicks", operatingTicks);
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        super.handlePacketData(data);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            operatingTicks = data.readInt();
            clientEnergyUsed = data.readDouble();
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(operatingTicks);
        data.add(clientEnergyUsed);
        return data;
    }

    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }
}
