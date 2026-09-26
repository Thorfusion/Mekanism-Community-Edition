package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.EnumColor;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.SideData;
import mekanism.common.base.IComparatorSupport;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITierUpgradeable;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.integration.computer.IComputerIntegration;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tier.BaseTier;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import mekanism.common.util.GasUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.TileUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.UltimateBlocks;
import mekanism.ultimate.common.block.BlockChemicalTank;
import mekanism.ultimate.common.capability.UltimateChemicalCapabilities;
import mekanism.ultimate.common.config.UltimateChemicalTankConfig;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.TieredChemicalTank;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import mekanism.ultimate.common.integration.mekanism.LongBackedMekGasTank;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Ultimate-owned modern Chemical Tank. The facade tank is authoritative; the
 * old Mek gas view is a bounded adapter used by existing tubes and containers.
 */
public class TileEntityChemicalTank extends TileEntityContainerBlock implements IGasHandler,
      IChemicalHandlerCE, IRedstoneControl, ISideConfiguration, ISecurityTile,
      ITierUpgradeable, IComputerIntegration, IComparatorSupport {

    private static final String NBT_TIER = "ChemicalTankTier";
    private static final String NBT_TANK = "ChemicalTank";
    private static final String NBT_DUMPING = "dumping";
    private static final String NBT_CONTROL = "controlType";
    private static final String[] METHODS = {"getCapacity", "getStored", "getChemical"};

    public ChemicalTankTier tier;
    public GasMode dumping = GasMode.IDLE;
    public RedstoneControl controlType = RedstoneControl.DISABLED;

    public final TieredChemicalTank chemicalTank;
    public final LongBackedMekGasTank gasTank;

    public final TileComponentEjector ejectorComponent;
    public final TileComponentConfig configComponent;
    public final TileComponentSecurity securityComponent;

    private long currentAmount = -1;
    private String currentIdentity = "";
    private int currentRedstoneLevel;

    public TileEntityChemicalTank() {
        this(ChemicalTankTier.BASIC);
    }

    public TileEntityChemicalTank(ChemicalTankTier tier) {
        super("ChemicalTank");
        this.tier = tier == null ? ChemicalTankTier.BASIC : tier;
        chemicalTank = new TieredChemicalTank(this.tier.getStorage(), this.tier.isCreative());
        gasTank = new LongBackedMekGasTank(chemicalTank);

        configComponent = new TileComponentConfig(this, TransmissionType.GAS, TransmissionType.ITEM);
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Fill", EnumColor.DARK_BLUE, new int[]{0}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Empty", EnumColor.DARK_RED, new int[]{1}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[]{2, 1, 0, 0, 0, 0});
        configComponent.setCanEject(TransmissionType.ITEM, false);
        // The legacy GAS side layout is also the facade CHEMICAL side layout.
        configComponent.setIOConfig(TransmissionType.GAS);
        configComponent.setEjecting(TransmissionType.GAS, true);

        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        ejectorComponent = new TileComponentEjector(this);
        securityComponent = new TileComponentSecurity(this);
    }

    @Override
    public void onUpdate() {
        if (world.isRemote) {
            return;
        }
        if (getBlockType() instanceof BlockChemicalTank) {
            tier = ((BlockChemicalTank) getBlockType()).getTier();
        }
        chemicalTank.configure(tier.getStorage(), tier.isCreative());
        handleItemIO();
        autoEject();
        applyDumping();

        IChemicalStackCE stored = chemicalTank.getStack();
        long amount = chemicalTank.getStored();
        String identity = stored == null ? ""
              : stored.getType().getKind() + ":" + stored.getType().getRegistryName();
        if (amount != currentAmount || !identity.equals(currentIdentity)) {
            currentAmount = amount;
            currentIdentity = identity;
            MekanismUtils.saveChunk(this);
        }
        int redstoneLevel = getRedstoneLevel();
        if (redstoneLevel != currentRedstoneLevel) {
            currentRedstoneLevel = redstoneLevel;
            markDirty();
            world.updateComparatorOutputLevel(pos, getBlockType());
        }
    }

    private void handleItemIO() {
        // Old gas containers remain first-class through the bounded gas view.
        TileUtils.drawGas(inventory.get(0), gasTank, !tier.isCreative());
        TileUtils.receiveGas(inventory.get(1), gasTank);
        handleFacadeContainer(inventory.get(0), true);
        handleFacadeContainer(inventory.get(1), false);
    }

    private void handleFacadeContainer(ItemStack stack, boolean fillContainer) {
        if (stack.isEmpty() || UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY == null
              || !stack.hasCapability(UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null)) {
            return;
        }
        IChemicalHandlerCE handler = stack.getCapability(
              UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null);
        if (handler == null) {
            return;
        }
        if (fillContainer) {
            IChemicalStackCE stored = chemicalTank.getStack();
            if (stored == null || stored.getType().getKind() == ChemicalKind.GAS) {
                return;
            }
            transferToHandler(handler, stored, tier.getOutput(), null);
        } else {
            transferFromHandler(handler, tier.getOutput());
        }
    }

    private long transferToHandler(IChemicalHandlerCE target, IChemicalStackCE stored, long limit,
          @Nullable EnumFacing side) {
        long remaining = Math.min(limit, stored.getAmount());
        long moved = 0;
        for (int tank = 0; tank < target.getChemicalTankCount(side) && remaining > 0; tank++) {
            if (!target.canInsertChemical(tank, stored.getType(), side)) {
                continue;
            }
            IChemicalTankCE targetTank = target.getChemicalTank(tank, side);
            long accepted = targetTank.insert(new ChemicalStackCE(stored.getType(), remaining), Action.SIMULATE);
            if (accepted > 0) {
                accepted = targetTank.insert(new ChemicalStackCE(stored.getType(), accepted), Action.EXECUTE);
                chemicalTank.extract(accepted, Action.EXECUTE);
                remaining -= accepted;
                moved += accepted;
            }
        }
        return moved;
    }

    private void transferFromHandler(IChemicalHandlerCE source, long limit) {
        long remaining = limit;
        for (int tank = 0; tank < source.getChemicalTankCount(null) && remaining > 0; tank++) {
            IChemicalTankCE sourceTank = source.getChemicalTank(tank, null);
            IChemicalStackCE available = sourceTank.getStack();
            if (available == null || available.getType().getKind() == ChemicalKind.GAS
                  || !source.canExtractChemical(tank, available.getType(), null)) {
                continue;
            }
            long accepted = chemicalTank.insert(new ChemicalStackCE(available.getType(),
                  Math.min(remaining, available.getAmount())), Action.SIMULATE);
            IChemicalStackCE extracted = sourceTank.extract(accepted, Action.EXECUTE);
            if (extracted != null) {
                chemicalTank.insert(extracted, Action.EXECUTE);
                remaining -= extracted.getAmount();
            }
        }
    }

    private void autoEject() {
        IChemicalStackCE stored = chemicalTank.getStack();
        if (stored == null || !MekanismUtils.canFunction(this)
              || dumping == GasMode.DUMPING || !configComponent.isEjecting(TransmissionType.GAS)) {
            return;
        }
        long output = Math.min(stored.getAmount(), tier.getOutput());
        if (stored.getType() instanceof MekGasChemicalType) {
            Gas gas = ((MekGasChemicalType) stored.getType()).getGas();
            int offered = (int) Math.min(Integer.MAX_VALUE, output);
            int sent = GasUtils.emit(new GasStack(gas, offered), this,
                  configComponent.getSidesForData(TransmissionType.GAS, facing, 2));
            chemicalTank.extract(sent, Action.EXECUTE);
        } else {
            emitFacadeChemical(stored, output);
        }
    }

    private void emitFacadeChemical(IChemicalStackCE stored, long output) {
        long remaining = output;
        for (EnumFacing side : configComponent.getSidesForData(TransmissionType.GAS, facing, 2)) {
            if (remaining <= 0) {
                break;
            }
            TileEntity targetTile = world.getTileEntity(pos.offset(side));
            EnumFacing targetSide = side.getOpposite();
            if (targetTile == null || UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY == null
                  || !targetTile.hasCapability(UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, targetSide)) {
                continue;
            }
            IChemicalHandlerCE target = targetTile.getCapability(
                  UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, targetSide);
            if (target == null) {
                continue;
            }
            remaining -= transferToHandler(target, stored, remaining, targetSide);
            stored = chemicalTank.getStack();
            if (stored == null) {
                break;
            }
        }
    }

    private void applyDumping() {
        if (tier.isCreative() || dumping == GasMode.IDLE || chemicalTank.getStack() == null) {
            return;
        }
        if (dumping == GasMode.DUMPING) {
            chemicalTank.extract(Math.max(1L, tier.getStorage() / 400L), Action.EXECUTE);
        } else {
            long target = (long) Math.min(Long.MAX_VALUE,
                  tier.getStorage() * UltimateChemicalTankConfig.dumpExcessKeepRatio);
            long excess = chemicalTank.getStored() - target;
            if (excess > 0) {
                chemicalTank.extract(Math.min(excess, tier.getOutput()), Action.EXECUTE);
            }
        }
    }

    @Override
    public boolean upgrade(BaseTier upgradeTier) {
        ChemicalTankTier[] tiers = ChemicalTankTier.values();
        if (upgradeTier == null || tier.ordinal() + 1 >= tiers.length
              || tiers[tier.ordinal() + 1].getBaseTier() != upgradeTier || world == null) {
            return false;
        }
        NBTTagCompound data = writeToNBT(new NBTTagCompound());
        ChemicalTankTier next = tiers[tier.ordinal() + 1];
        world.setBlockState(pos, UltimateBlocks.getChemicalTank(next).getDefaultState(), 3);
        TileEntity replacement = world.getTileEntity(pos);
        if (!(replacement instanceof TileEntityChemicalTank)) {
            return false;
        }
        data.setInteger(NBT_TIER, next.ordinal());
        ((TileEntityChemicalTank) replacement).readFromNBT(data);
        replacement.markDirty();
        Mekanism.packetHandler.sendUpdatePacket((TileEntityChemicalTank) replacement);
        return true;
    }

    @Nonnull
    @Override
    public String getName() {
        return LangUtils.localize("tile.ChemicalTank" + tier.getBaseTier().getSimpleName() + ".name");
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side) {
        if (slot == 1) {
            return stack.getItem() instanceof IGasItem && ((IGasItem) stack.getItem()).getGas(stack) == null
                  || isFacadeContainer(stack);
        }
        return slot == 0 && (stack.getItem() instanceof IGasItem
              && ((IGasItem) stack.getItem()).getGas(stack) != null || isFacadeContainer(stack));
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (stack.getItem() instanceof IGasItem) {
            Gas type = gasTank.getGasType();
            boolean validGas = slot == 0 ? ((IGasItem) stack.getItem()).canReceiveGas(stack, type)
                  : slot == 1 && ((IGasItem) stack.getItem()).canProvideGas(stack, type);
            if (validGas) {
                return true;
            }
        }
        return isFacadeContainer(stack);
    }

    private static boolean isFacadeContainer(ItemStack stack) {
        return UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY != null
              && stack.hasCapability(UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null);
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return configComponent.getOutput(TransmissionType.ITEM, side, facing).availableSlots;
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        if (stack == null || stack.getGas() == null || stack.amount <= 0 || !allowsInput(side)) {
            return 0;
        }
        return (int) chemicalTank.insert(new ChemicalStackCE(new MekGasChemicalType(stack.getGas()), stack.amount),
              doTransfer ? Action.EXECUTE : Action.SIMULATE);
    }

    @Nullable
    @Override
    public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
        if (amount <= 0 || !allowsOutput(side) || !(chemicalTank.getStack() != null
              && chemicalTank.getStack().getType() instanceof MekGasChemicalType)) {
            return null;
        }
        IChemicalStackCE extracted = chemicalTank.extract(amount,
              doTransfer ? Action.EXECUTE : Action.SIMULATE);
        return extracted == null ? null : new GasStack(
              ((MekGasChemicalType) extracted.getType()).getGas(), (int) extracted.getAmount());
    }

    @Override
    public boolean canDrawGas(EnumFacing side, Gas type) {
        IChemicalStackCE stored = chemicalTank.getStack();
        return allowsOutput(side) && stored != null && stored.getType() instanceof MekGasChemicalType
              && (type == null || ((MekGasChemicalType) stored.getType()).getGas() == type);
    }

    @Override
    public boolean canReceiveGas(EnumFacing side, Gas type) {
        return type != null && allowsInput(side) && chemicalTank.insert(
              new ChemicalStackCE(new MekGasChemicalType(type), 1), Action.SIMULATE) > 0;
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        return new GasTankInfo[]{gasTank};
    }

    @Override
    public int getChemicalTankCount(@Nullable EnumFacing side) {
        return 1;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank, @Nullable EnumFacing side) {
        if (tank != 0) {
            throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
        }
        return chemicalTank;
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        return tank == 0 && type != null && allowsInput(side)
              && chemicalTank.insert(new ChemicalStackCE(type, 1), Action.SIMULATE) > 0;
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        IChemicalStackCE stored = chemicalTank.getStack();
        return tank == 0 && type != null && allowsOutput(side) && stored != null
              && TieredChemicalTank.isSameType(stored.getType(), type);
    }

    private boolean allowsInput(@Nullable EnumFacing side) {
        return side == null || configComponent.hasSideForData(TransmissionType.GAS, facing, 1, side);
    }

    private boolean allowsOutput(@Nullable EnumFacing side) {
        return side == null || configComponent.hasSideForData(TransmissionType.GAS, facing, 2, side);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return false;
        }
        return capability == Capabilities.GAS_HANDLER_CAPABILITY
              || capability == UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY
              || super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return null;
        }
        if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
            return Capabilities.GAS_HANDLER_CAPABILITY.cast(this);
        }
        if (capability == UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY) {
            return UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean isCapabilityDisabled(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY) {
            return side != null && configComponent.hasSideForData(TransmissionType.GAS, facing, 0, side);
        }
        return configComponent.isCapabilityDisabled(capability, side, facing)
              || super.isCapabilityDisabled(capability, side);
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            if (data.readInt() == 0) {
                dumping = GasMode.values()[(dumping.ordinal() + 1) % GasMode.values().length];
                markDirty();
            }
            for (EntityPlayer player : playersUsing) {
                Mekanism.packetHandler.sendTo(new TileEntityMessage(this), (EntityPlayerMP) player);
            }
            return;
        }
        super.handlePacketData(data);
        int tierIndex = data.readInt();
        tier = safeTier(tierIndex);
        chemicalTank.configure(tier.getStorage(), tier.isCreative());
        chemicalTank.readFromNBT(PacketHandler.readNBT(data), UltimateChemicalRegistry.INSTANCE);
        dumping = safeDumpMode(data.readInt());
        controlType = safeControl(data.readInt());
        MekanismUtils.updateBlock(world, pos);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tier = safeTier(data.getInteger(NBT_TIER));
        chemicalTank.configure(tier.getStorage(), tier.isCreative());
        chemicalTank.readFromNBT(data.getCompoundTag(NBT_TANK), UltimateChemicalRegistry.INSTANCE);
        dumping = safeDumpMode(data.getInteger(NBT_DUMPING));
        controlType = safeControl(data.getInteger(NBT_CONTROL));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger(NBT_TIER, tier.ordinal());
        NBTTagCompound tankData = new NBTTagCompound();
        chemicalTank.writeToNBT(tankData);
        data.setTag(NBT_TANK, tankData);
        data.setInteger(NBT_DUMPING, dumping.ordinal());
        data.setInteger(NBT_CONTROL, controlType.ordinal());
        return data;
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(tier.ordinal());
        NBTTagCompound tankData = new NBTTagCompound();
        chemicalTank.writeToNBT(tankData);
        data.add(tankData);
        data.add(dumping.ordinal());
        data.add(controlType.ordinal());
        return data;
    }

    private static ChemicalTankTier safeTier(int index) {
        ChemicalTankTier[] values = ChemicalTankTier.values();
        return index >= 0 && index < values.length ? values[index] : ChemicalTankTier.BASIC;
    }

    private static GasMode safeDumpMode(int index) {
        GasMode[] values = GasMode.values();
        return index >= 0 && index < values.length ? values[index] : GasMode.IDLE;
    }

    private static RedstoneControl safeControl(int index) {
        RedstoneControl[] values = RedstoneControl.values();
        return index >= 0 && index < values.length ? values[index] : RedstoneControl.DISABLED;
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing side) {
        return side.getAxis().isHorizontal();
    }

    @Override
    public int getRedstoneLevel() {
        long capacity = chemicalTank.getCapacity();
        long stored = chemicalTank.getStored();
        if (stored <= 0 || capacity <= 0) {
            return 0;
        }
        return Math.min(15, 1 + (int) Math.floor(14D * ((double) stored / capacity)));
    }

    @Override
    public RedstoneControl getControlType() {
        return controlType;
    }

    @Override
    public void setControlType(RedstoneControl type) {
        controlType = type == null ? RedstoneControl.DISABLED : type;
    }

    @Override
    public boolean canPulse() {
        return false;
    }

    @Override
    public TileComponentEjector getEjector() {
        return ejectorComponent;
    }

    @Override
    public TileComponentConfig getConfig() {
        return configComponent;
    }

    @Override
    public EnumFacing getOrientation() {
        return facing;
    }

    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }

    @Override
    public String[] getMethods() {
        return METHODS;
    }

    @Override
    public Object[] invoke(int method, Object[] arguments) throws NoSuchMethodException {
        switch (method) {
            case 0:
                return new Object[]{chemicalTank.getCapacity()};
            case 1:
                return new Object[]{chemicalTank.getStored()};
            case 2:
                IChemicalStackCE stack = chemicalTank.getStack();
                return new Object[]{stack == null ? "" : stack.getType().getRegistryName()};
            default:
                throw new NoSuchMethodException();
        }
    }

    public enum GasMode {
        IDLE("gui.idle"),
        DUMPING_EXCESS("gui.dumping_excess"),
        DUMPING("gui.dumping");

        private final String langKey;

        GasMode(String langKey) {
            this.langKey = langKey;
        }

        public String getLangKey() {
            return langKey;
        }

        public static <T> T choose(GasMode mode, T idle, T dumpingExcess, T dumping) {
            return mode == DUMPING ? dumping : mode == DUMPING_EXCESS ? dumpingExcess : idle;
        }
    }
}
