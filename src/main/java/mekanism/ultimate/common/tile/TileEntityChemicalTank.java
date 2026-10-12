package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.Range4D;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.GasTransmission;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import mekanism.api.gas.IGasTankInfoProvider;
import mekanism.api.gas.ITubeConnection;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.SideData;
import mekanism.common.Tier.BaseTier;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITierUpgradeable;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.TileEntityContainerBlock;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.UltimateBlocks;
import mekanism.ultimate.common.block.BlockChemicalTank;
import mekanism.ultimate.common.config.UltimateChemicalTankConfig;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.TieredChemicalTank;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import mekanism.ultimate.common.integration.mekanism.LongBackedMekGasTank;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.item.ItemBlockChemicalTank;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import mekanism.ultimate.common.util.ChemicalTransferUtilsCE;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Ultimate-owned modern Chemical Tank. The facade tank is authoritative; the
 * old Mek gas view is a bounded adapter used by existing tubes and containers.
 */
public class TileEntityChemicalTank extends TileEntityContainerBlock
      implements IGasHandler, IGasTankInfoProvider, ITubeConnection,
      IChemicalHandlerCE, IRedstoneControl, ISideConfiguration,
      ISecurityTile, ITierUpgradeable
{
    private static final String NBT_TIER = "ChemicalTankTier";
    private static final String NBT_TANK = "ChemicalTank";
    private static final String NBT_DUMPING = "dumping";
    private static final String NBT_CONTROL = "controlType";

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

    public TileEntityChemicalTank()
    {
        this(ChemicalTankTier.BASIC);
    }

    public TileEntityChemicalTank(ChemicalTankTier tier)
    {
        super("ChemicalTank");
        this.tier = tier == null ? ChemicalTankTier.BASIC : tier;
        chemicalTank = new TieredChemicalTank(this.tier.getStorage(),
              this.tier.isCreative());
        gasTank = new LongBackedMekGasTank(chemicalTank);

        configComponent = new TileComponentConfig(this,
              TransmissionType.GAS, TransmissionType.ITEM);
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Fill", EnumColor.DARK_BLUE, new int[] {0}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Empty", EnumColor.DARK_RED, new int[] {1}));
        configComponent.setConfig(TransmissionType.ITEM,
              new byte[] {2, 1, 0, 0, 0, 0});
        configComponent.setCanEject(TransmissionType.ITEM, false);
        // The legacy GAS side layout is also the facade CHEMICAL layout.
        configComponent.setIOConfig(TransmissionType.GAS);
        configComponent.setEjecting(TransmissionType.GAS, true);

        inventory = new ItemStack[2];
        ejectorComponent = new TileComponentEjector(this);
        securityComponent = new TileComponentSecurity(this);
    }

    public void setTier(ChemicalTankTier newTier)
    {
        tier = newTier == null ? ChemicalTankTier.BASIC : newTier;
        chemicalTank.configure(tier.getStorage(), tier.isCreative());
    }

    @Override
    public void onUpdate()
    {
        if (worldObj.isRemote)
        {
            return;
        }
        Block block = getBlockType();
        if (block instanceof BlockChemicalTank)
        {
            setTier(((BlockChemicalTank)block).getTier());
        }

        handleItemIO();
        autoEject();
        applyDumping();

        IChemicalStackCE stored = chemicalTank.getStack();
        long amount = chemicalTank.getStored();
        String identity = stored == null ? ""
              : stored.getType().getKind() + ":"
                    + stored.getType().getRegistryName();
        if (amount != currentAmount || !identity.equals(currentIdentity))
        {
            currentAmount = amount;
            currentIdentity = identity;
            MekanismUtils.saveChunk(this);
        }

        int redstoneLevel = getRedstoneLevel();
        if (redstoneLevel != currentRedstoneLevel)
        {
            currentRedstoneLevel = redstoneLevel;
            markDirty();
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,
                  getBlockType());
        }
    }

    private void handleItemIO()
    {
        fillContainer(inventory[0]);
        emptyContainer(inventory[1]);
    }

    private void fillContainer(ItemStack stack)
    {
        if (stack == null || chemicalTank.getStack() == null)
        {
            return;
        }
        if (stack.getItem() instanceof ItemBlockChemicalTank)
        {
            ChemicalTransferUtilsCE.fillItem(stack, chemicalTank,
                  tier.getOutput());
            return;
        }
        if (stack.getItem() instanceof IGasItem
              && chemicalTank.getStack().getType()
                    instanceof MekGasChemicalType)
        {
            Gas gas = ((MekGasChemicalType)chemicalTank.getStack().getType())
                  .getGas();
            int offered = saturatingInt(Math.min(chemicalTank.getStored(),
                  tier.getOutput()));
            int accepted = GasTransmission.addGas(stack,
                  new GasStack(gas, offered));
            chemicalTank.extract(accepted, Action.EXECUTE);
        }
    }

    private void emptyContainer(ItemStack stack)
    {
        if (stack == null)
        {
            return;
        }
        if (stack.getItem() instanceof ItemBlockChemicalTank)
        {
            IChemicalHandlerCE handler = ChemicalTransferUtilsCE
                  .itemHandler(stack);
            IChemicalStackCE stored = handler == null ? null
                  : handler.getChemicalTank(0, null).getStack();
            if (stored != null)
            {
                ChemicalTransferUtilsCE.drainItem(stack, chemicalTank,
                      tier.getOutput(), stored.getType().getKind());
            }
            return;
        }
        if (!(stack.getItem() instanceof IGasItem))
        {
            return;
        }
        IGasItem gasItem = (IGasItem)stack.getItem();
        GasStack available = gasItem.getGas(stack);
        if (available == null || available.getGas() == null)
        {
            return;
        }
        int offered = Math.min(available.amount,
              saturatingInt(tier.getOutput()));
        int accepted = gasTank.receive(new GasStack(available.getGas(),
              offered), false);
        if (accepted > 0)
        {
            GasStack removed = gasItem.removeGas(stack, accepted);
            if (removed != null)
            {
                gasTank.receive(removed, true);
            }
        }
    }

    private void autoEject()
    {
        IChemicalStackCE stored = chemicalTank.getStack();
        if (stored == null || !MekanismUtils.canFunction(this)
              || dumping == GasMode.DUMPING
              || !configComponent.isEjecting(TransmissionType.GAS))
        {
            return;
        }
        long output = Math.min(stored.getAmount(), tier.getOutput());
        if (stored.getType() instanceof MekGasChemicalType)
        {
            Gas gas = ((MekGasChemicalType)stored.getType()).getGas();
            int sent = GasTransmission.emit(new GasStack(gas,
                        saturatingInt(output)), this,
                  configComponent.getSidesForData(TransmissionType.GAS,
                        facing, 2));
            chemicalTank.extract(sent, Action.EXECUTE);
        }
        else
        {
            ChemicalTransferUtilsCE.emit(this, chemicalTank, output,
                  configComponent.getSidesForData(TransmissionType.GAS,
                        facing, 2));
        }
    }

    private void applyDumping()
    {
        if (tier.isCreative() || dumping == GasMode.IDLE
              || chemicalTank.getStack() == null)
        {
            return;
        }
        if (dumping == GasMode.DUMPING)
        {
            chemicalTank.extract(Math.max(1L, tier.getStorage() / 400L),
                  Action.EXECUTE);
        }
        else
        {
            long target = (long)Math.min(Long.MAX_VALUE,
                  tier.getStorage()
                        * UltimateChemicalTankConfig.dumpExcessKeepRatio);
            long excess = chemicalTank.getStored() - target;
            if (excess > 0)
            {
                chemicalTank.extract(Math.min(excess, tier.getOutput()),
                      Action.EXECUTE);
            }
        }
    }

    @Override
    public boolean upgrade(BaseTier upgradeTier)
    {
        ChemicalTankTier[] tiers = ChemicalTankTier.values();
        if (upgradeTier == null || tier.ordinal() + 1 >= tiers.length
              || tiers[tier.ordinal() + 1].getBaseTier() != upgradeTier
              || worldObj == null)
        {
            return false;
        }
        NBTTagCompound data = new NBTTagCompound();
        writeToNBT(data);
        ChemicalTankTier next = tiers[tier.ordinal() + 1];
        if (!worldObj.setBlock(xCoord, yCoord, zCoord,
              UltimateBlocks.getChemicalTank(next), 0, 3))
        {
            return false;
        }
        TileEntity replacement = worldObj.getTileEntity(xCoord, yCoord,
              zCoord);
        if (!(replacement instanceof TileEntityChemicalTank))
        {
            return false;
        }
        data.setInteger(NBT_TIER, next.ordinal());
        TileEntityChemicalTank tank = (TileEntityChemicalTank)replacement;
        tank.readFromNBT(data);
        tank.setTier(next);
        tank.markDirty();
        Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(
                    Coord4D.get(tank), tank.getNetworkedData(new ArrayList())),
              new Range4D(Coord4D.get(tank)));
        return true;
    }

    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("tile.ChemicalTank"
              + tier.getBaseTier().getName() + ".name");
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        if (stack == null)
        {
            return false;
        }
        if (stack.getItem() instanceof ItemBlockChemicalTank)
        {
            IChemicalTankCE itemTank = ((ItemBlockChemicalTank)stack
                  .getItem()).getChemicalHandler(stack)
                        .getChemicalTank(0, null);
            return slot == 1 ? itemTank.getStack() == null
                  : slot == 0 && itemTank.getStack() != null
                        && itemTank.getStored() >= itemTank.getCapacity();
        }
        if (!(stack.getItem() instanceof IGasItem))
        {
            return false;
        }
        IGasItem item = (IGasItem)stack.getItem();
        GasStack gas = item.getGas(stack);
        return slot == 1 ? gas == null
              : slot == 0 && gas != null
                    && gas.amount >= item.getMaxGas(stack);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }
        if (stack.getItem() instanceof ItemBlockChemicalTank)
        {
            return slot == 0 || slot == 1;
        }
        if (stack.getItem() instanceof IGasItem)
        {
            Gas type = gasTank.getGasType();
            return slot == 0
                  ? ((IGasItem)stack.getItem()).canReceiveGas(stack, type)
                  : slot == 1 && ((IGasItem)stack.getItem())
                        .canProvideGas(stack, type);
        }
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return configComponent.getOutput(TransmissionType.ITEM, side,
              facing).availableSlots;
    }

    @Override
    public int receiveGas(ForgeDirection side, GasStack stack,
          boolean doTransfer)
    {
        if (stack == null || stack.getGas() == null || stack.amount <= 0
              || !allowsInput(side))
        {
            return 0;
        }
        return saturatingInt(chemicalTank.insert(new ChemicalStackCE(
                    new MekGasChemicalType(stack.getGas()), stack.amount),
              doTransfer ? Action.EXECUTE : Action.SIMULATE));
    }

    @Deprecated
    @Override
    public int receiveGas(ForgeDirection side, GasStack stack)
    {
        return receiveGas(side, stack, true);
    }

    @Nullable
    @Override
    public GasStack drawGas(ForgeDirection side, int amount,
          boolean doTransfer)
    {
        IChemicalStackCE stored = chemicalTank.getStack();
        if (amount <= 0 || !allowsOutput(side) || stored == null
              || !(stored.getType() instanceof MekGasChemicalType))
        {
            return null;
        }
        IChemicalStackCE extracted = chemicalTank.extract(amount,
              doTransfer ? Action.EXECUTE : Action.SIMULATE);
        return extracted == null ? null : new GasStack(
              ((MekGasChemicalType)extracted.getType()).getGas(),
              saturatingInt(extracted.getAmount()));
    }

    @Deprecated
    @Nullable
    @Override
    public GasStack drawGas(ForgeDirection side, int amount)
    {
        return drawGas(side, amount, true);
    }

    @Override
    public boolean canDrawGas(ForgeDirection side, Gas type)
    {
        IChemicalStackCE stored = chemicalTank.getStack();
        return allowsOutput(side) && stored != null
              && stored.getType() instanceof MekGasChemicalType
              && (type == null || ((MekGasChemicalType)stored.getType())
                    .getGas() == type);
    }

    @Override
    public boolean canReceiveGas(ForgeDirection side, Gas type)
    {
        return type != null && allowsInput(side) && chemicalTank.insert(
              new ChemicalStackCE(new MekGasChemicalType(type), 1),
              Action.SIMULATE) > 0;
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo()
    {
        return new GasTankInfo[] {gasTank};
    }

    @Override
    public boolean canTubeConnect(ForgeDirection side)
    {
        return side != null && side != ForgeDirection.UNKNOWN
              && (allowsInput(side) || allowsOutput(side));
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
        return chemicalTank;
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        return tank == 0 && type != null && allowsInput(side)
              && chemicalTank.insert(new ChemicalStackCE(type, 1),
                    Action.SIMULATE) > 0;
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        IChemicalStackCE stored = chemicalTank.getStack();
        return tank == 0 && type != null && allowsOutput(side)
              && stored != null && TieredChemicalTank.isSameType(
                    stored.getType(), type);
    }

    private boolean allowsInput(@Nullable ForgeDirection side)
    {
        return side == null || configComponent.getSidesForData(
              TransmissionType.GAS, facing, 1).contains(side);
    }

    private boolean allowsOutput(@Nullable ForgeDirection side)
    {
        return side == null || configComponent.getSidesForData(
              TransmissionType.GAS, facing, 2).contains(side);
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        if (!worldObj.isRemote)
        {
            if (data.readInt() == 0)
            {
                dumping = GasMode.values()[
                      (dumping.ordinal() + 1) % GasMode.values().length];
                markDirty();
            }
            for (EntityPlayer player : playersUsing)
            {
                Mekanism.packetHandler.sendTo(new TileEntityMessage(
                      Coord4D.get(this), getNetworkedData(new ArrayList())),
                      (EntityPlayerMP)player);
            }
            return;
        }

        super.handlePacketData(data);
        setTier(safeTier(data.readInt()));
        chemicalTank.clear();
        if (data.readBoolean())
        {
            ChemicalKind kind = safeKind(data.readInt());
            String name = mekanism.common.PacketHandler.readString(data);
            long amount = readLong(data);
            IChemicalTypeCE type = UltimateChemicalRegistry.INSTANCE.resolve(
                  kind, name);
            if (type != null && amount > 0)
            {
                chemicalTank.insert(new ChemicalStackCE(type, amount),
                      Action.EXECUTE);
            }
        }
        dumping = GasMode.byIndex(data.readInt());
        controlType = safeControl(data.readInt());
        MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        setTier(safeTier(data.getInteger(NBT_TIER)));
        chemicalTank.readFromNBT(data.getCompoundTag(NBT_TANK),
              UltimateChemicalRegistry.INSTANCE);
        dumping = GasMode.byIndex(data.getInteger(NBT_DUMPING));
        controlType = safeControl(data.getInteger(NBT_CONTROL));
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setInteger(NBT_TIER, tier.ordinal());
        NBTTagCompound tankData = new NBTTagCompound();
        chemicalTank.writeToNBT(tankData);
        data.setTag(NBT_TANK, tankData);
        data.setInteger(NBT_DUMPING, dumping.ordinal());
        data.setInteger(NBT_CONTROL, controlType.ordinal());
    }

    @Override
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(tier.ordinal());
        IChemicalStackCE stored = chemicalTank.getStack();
        data.add(stored != null);
        if (stored != null)
        {
            data.add(stored.getType().getKind().ordinal());
            data.add(stored.getType().getRegistryName());
            writeLong(data, stored.getAmount());
        }
        data.add(dumping.ordinal());
        data.add(controlType.ordinal());
        return data;
    }

    private static ChemicalTankTier safeTier(int index)
    {
        ChemicalTankTier[] values = ChemicalTankTier.values();
        return index >= 0 && index < values.length ? values[index]
              : ChemicalTankTier.BASIC;
    }

    private static ChemicalKind safeKind(int index)
    {
        ChemicalKind[] values = ChemicalKind.values();
        return index >= 0 && index < values.length ? values[index]
              : ChemicalKind.GAS;
    }

    private static RedstoneControl safeControl(int index)
    {
        RedstoneControl[] values = RedstoneControl.values();
        return index >= 0 && index < values.length ? values[index]
              : RedstoneControl.DISABLED;
    }

    private static void writeLong(ArrayList data, long value)
    {
        data.add((int)(value >>> 32));
        data.add((int)value);
    }

    private static long readLong(ByteBuf data)
    {
        return ((long)data.readInt() << 32)
              | (data.readInt() & 0xFFFFFFFFL);
    }

    private static int saturatingInt(long value)
    {
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)value;
    }

    @Override
    public boolean canSetFacing(int side)
    {
        return side != 0 && side != 1;
    }

    public int getRedstoneLevel()
    {
        long capacity = chemicalTank.getCapacity();
        long stored = chemicalTank.getStored();
        if (stored <= 0 || capacity <= 0)
        {
            return 0;
        }
        double fraction = (double)stored / (double)capacity;
        return Math.min(15, MathHelper.floor_double(fraction * 14D) + 1);
    }

    @Override
    public RedstoneControl getControlType()
    {
        return controlType;
    }

    @Override
    public void setControlType(RedstoneControl type)
    {
        controlType = type == null ? RedstoneControl.DISABLED : type;
    }

    @Override
    public boolean canPulse()
    {
        return false;
    }

    @Override
    public TileComponentEjector getEjector()
    {
        return ejectorComponent;
    }

    @Override
    public TileComponentConfig getConfig()
    {
        return configComponent;
    }

    @Override
    public int getOrientation()
    {
        return facing;
    }

    @Override
    public TileComponentSecurity getSecurity()
    {
        return securityComponent;
    }

    public enum GasMode
    {
        IDLE,
        DUMPING_EXCESS,
        DUMPING;

        public static GasMode byIndex(int index)
        {
            GasMode[] values = values();
            return index >= 0 && index < values.length ? values[index] : IDLE;
        }
    }
}
