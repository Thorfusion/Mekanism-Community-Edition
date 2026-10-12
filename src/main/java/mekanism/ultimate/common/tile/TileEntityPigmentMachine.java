package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.general;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.Upgrade;
import mekanism.common.Upgrade.IUpgradeInfoHandler;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.content.chemical.PigmentTypeCE;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

/** Shared 1.7 machine state for the three Ultimate pigment machines. */
public abstract class TileEntityPigmentMachine
      extends TileEntityElectricBlock
      implements IChemicalHandlerCE, IUpgradeTile, IUpgradeInfoHandler,
      IRedstoneControl, IActiveState, ISecurityTile
{
    public static final long TANK_CAPACITY = 20_000L;

    public final double BASE_ENERGY_PER_TICK;
    public double energyPerTick;
    public double clientEnergyUsed;
    public double prevEnergy;

    public boolean isActive;
    public boolean clientActive;
    public int updateDelay;
    public int operatingTicks;
    public RedstoneControl controlType = RedstoneControl.DISABLED;

    public final TileComponentUpgrade upgradeComponent;
    public final TileComponentSecurity securityComponent;

    private final String translationKey;
    private int currentRedstoneLevel = -1;

    protected TileEntityPigmentMachine(String name, String translationKey,
          double capacity, double usage, int upgradeSlot)
    {
        super(name, capacity);
        this.translationKey = translationKey;
        BASE_ENERGY_PER_TICK = usage;
        energyPerTick = usage;
        upgradeComponent = new TileComponentUpgrade(this, upgradeSlot);
        upgradeComponent.setSupported(Upgrade.MUFFLING);
        securityComponent = new TileComponentSecurity(this);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (worldObj.isRemote)
        {
            if (updateDelay > 0)
            {
                updateDelay--;
            }
            return;
        }

        if (updateDelay > 0 && --updateDelay == 0
              && clientActive != isActive)
        {
            sendUpdatePacket();
        }
    }

    protected int ticksRequired(int baseTicks)
    {
        return Math.max(1, baseTicks >> upgradeComponent.getUpgrades(
              Upgrade.SPEED));
    }

    public double getProgress(int baseTicks)
    {
        return (double)operatingTicks / ticksRequired(baseTicks);
    }

    protected void updateComparator(LongChemicalTank tank)
    {
        int level = redstoneLevel(tank);
        if (level != currentRedstoneLevel)
        {
            currentRedstoneLevel = level;
            markDirty();
            worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
        }
    }

    protected static int redstoneLevel(IChemicalTankCE tank)
    {
        if (tank == null || tank.getStored() <= 0 || tank.getCapacity() <= 0)
        {
            return 0;
        }
        double fraction = (double)tank.getStored()
              / (double)tank.getCapacity();
        return Math.min(15, MathHelper.floor_double(fraction * 14D) + 1);
    }

    protected static void addTankSnapshot(ArrayList data,
          IChemicalTankCE tank)
    {
        IChemicalStackCE stack = tank.getStack();
        data.add(stack != null);
        if (stack != null)
        {
            data.add(stack.getType().getKind().ordinal());
            data.add(stack.getType().getRegistryName());
            writeLong(data, stack.getAmount());
        }
    }

    protected static void readTankSnapshot(ByteBuf data,
          LongChemicalTank tank)
    {
        tank.clear();
        if (!data.readBoolean())
        {
            return;
        }
        ChemicalKind[] kinds = ChemicalKind.values();
        int kindIndex = data.readInt();
        String name = PacketHandler.readString(data);
        long amount = readLong(data);
        if (kindIndex < 0 || kindIndex >= kinds.length || amount <= 0)
        {
            return;
        }
        IChemicalTypeCE type = UltimateChemicalRegistry.INSTANCE.resolve(
              kinds[kindIndex], name);
        if (type != null)
        {
            tank.insert(new ChemicalStackCE(type, amount), Action.EXECUTE);
        }
    }

    protected static void writeTank(NBTTagCompound data, String key,
          LongChemicalTank tank)
    {
        NBTTagCompound tankData = new NBTTagCompound();
        tank.writeToNBT(tankData);
        data.setTag(key, tankData);
    }

    protected static void readTank(NBTTagCompound data, String key,
          LongChemicalTank tank)
    {
        tank.readFromNBT(data.getCompoundTag(key),
              UltimateChemicalRegistry.INSTANCE);
    }

    @Override
    public void recalculateUpgradables(Upgrade upgrade)
    {
        super.recalculateUpgradables(upgrade);
        if (upgrade == Upgrade.SPEED || upgrade == Upgrade.ENERGY)
        {
            energyPerTick = MekanismUtils.getEnergyPerTick(this,
                  BASE_ENERGY_PER_TICK);
        }
        if (upgrade == Upgrade.ENERGY)
        {
            maxEnergy = MekanismUtils.getMaxEnergy(this, BASE_MAX_ENERGY);
            setEnergy(getEnergy());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        isActive = data.getBoolean("isActive");
        clientActive = isActive;
        operatingTicks = Math.max(0, data.getInteger("operatingTicks"));
        controlType = safeControl(data.getInteger("controlType"));
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("isActive", isActive);
        data.setInteger("operatingTicks", operatingTicks);
        data.setInteger("controlType", controlType.ordinal());
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            clientActive = data.readBoolean();
            controlType = safeControl(data.readInt());
            operatingTicks = Math.max(0, data.readInt());
            clientEnergyUsed = Math.max(0, data.readDouble());
            if (clientActive != isActive && updateDelay == 0)
            {
                updateDelay = general.UPDATE_DELAY;
                isActive = clientActive;
                MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(isActive);
        data.add(controlType.ordinal());
        data.add(operatingTicks);
        data.add(clientEnergyUsed);
        return data;
    }

    @Override
    public boolean canSetFacing(int side)
    {
        return side != ForgeDirection.DOWN.ordinal()
              && side != ForgeDirection.UP.ordinal();
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
        MekanismUtils.saveChunk(this);
    }

    @Override
    public boolean canPulse()
    {
        return false;
    }

    @Override
    public boolean getActive()
    {
        return isActive;
    }

    @Override
    public void setActive(boolean active)
    {
        isActive = active;
        if (clientActive != active && updateDelay == 0)
        {
            if (worldObj != null && !worldObj.isRemote)
            {
                sendUpdatePacket();
            }
            updateDelay = 10;
            clientActive = active;
        }
    }

    private void sendUpdatePacket()
    {
        if (worldObj != null)
        {
            Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(
                  Coord4D.get(this), getNetworkedData(new ArrayList())),
                  new Range4D(Coord4D.get(this)));
        }
    }

    @Override
    public boolean renderUpdate()
    {
        return true;
    }

    @Override
    public boolean lightUpdate()
    {
        return false;
    }

    @Override
    public TileComponentUpgrade getComponent()
    {
        return upgradeComponent;
    }

    @Override
    public TileComponentSecurity getSecurity()
    {
        return securityComponent;
    }

    @Override
    public List<String> getInfo(Upgrade upgrade)
    {
        return upgrade == Upgrade.SPEED
              ? upgrade.getExpScaledInfo(this)
              : upgrade.getMultScaledInfo(this);
    }

    @Override
    public String getInventoryName()
    {
        return LangUtils.localize(translationKey);
    }

    public static String getChemicalName(IChemicalTypeCE type)
    {
        return type instanceof PigmentTypeCE
              ? LangUtils.localize("chemical.pigment."
                    + ((PigmentTypeCE)type).getPath())
              : type == null ? LangUtils.localize("gui.empty")
                    : type.getRegistryName();
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
}
