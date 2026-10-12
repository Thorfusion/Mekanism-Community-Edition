package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import mekanism.api.Coord4D;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.IUpgradeItem;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.chunkloading.IChunkLoader;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.UltimateConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.ForgeDirection;

/** Energy-backed selectable 5x5 chunk loader. */
public final class TileEntityDimensionalStabilizer
      extends TileEntityElectricBlock implements IChunkLoader, IUpgradeTile,
      IRedstoneControl, IActiveState, ISecurityTile
{
    public static final int MAX_LOAD_RADIUS = 2;
    public static final int DIAMETER = MAX_LOAD_RADIUS * 2 + 1;
    public static final int ENERGY_SLOT = 0;
    public static final int UPGRADE_SLOT = 1;
    private static final int[] SLOTS = {ENERGY_SLOT, UPGRADE_SLOT};

    private final boolean[][] loadingChunks =
          new boolean[DIAMETER][DIAMETER];
    private final TileComponentUpgrade upgradeComponent =
          new TileComponentUpgrade(this, UPGRADE_SLOT);
    private final TileComponentSecurity securityComponent =
          new TileComponentSecurity(this);

    private Ticket chunkTicket;
    private int chunksLoaded = 1;
    private boolean active;
    private boolean clientActive;
    private double energyPerChunk = 5000D;
    public double clientEnergyUsed;
    public RedstoneControl controlType = RedstoneControl.DISABLED;

    public TileEntityDimensionalStabilizer()
    {
        super("DimensionalStabilizer",
              UltimateConfig.getDimensionalStabilizerMaxEnergy());
        inventory = new ItemStack[2];
        loadingChunks[MAX_LOAD_RADIUS][MAX_LOAD_RADIUS] = true;
        upgradeComponent.setSupported(Upgrade.SPEED, false);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (worldObj.isRemote)
        {
            return;
        }
        updateConfiguredEnergyValues();
        ChargeUtils.discharge(ENERGY_SLOT, this);
        double required = getEnergyUsage();
        boolean shouldRun = UltimateConfig
              .allowDimensionalStabilizerChunkloading
              && MekanismUtils.canFunction(this) && required > 0D
              && getEnergy() >= required;
        setActive(shouldRun);
        if (shouldRun)
        {
            ensureChunkTicket();
            if (chunkTicket != null)
            {
                setEnergy(getEnergy() - required);
                clientEnergyUsed = required;
            }
            else
            {
                setActive(false);
                clientEnergyUsed = 0D;
            }
        }
        else
        {
            clientEnergyUsed = 0D;
            releaseChunks();
        }
    }

    private void updateConfiguredEnergyValues()
    {
        double configured = Math.max(0D,
              UltimateConfig.dimensionalStabilizerEnergyPerChunk);
        double newUsage = MekanismUtils.getBaseEnergyPerTick(this,
              configured);
        double newMax = MekanismUtils.getMaxEnergy(this,
              UltimateConfig.getDimensionalStabilizerMaxEnergy());
        energyPerChunk = newUsage;
        if (maxEnergy != newMax)
        {
            maxEnergy = newMax;
            setEnergy(getEnergy());
        }
    }

    public double getEnergyUsage()
    {
        return energyPerChunk * chunksLoaded;
    }

    public int getChunksLoaded()
    {
        return chunksLoaded;
    }

    public boolean isChunkLoadingAt(int x, int z)
    {
        return x >= 0 && x < DIAMETER && z >= 0 && z < DIAMETER
              && loadingChunks[x][z];
    }

    public boolean toggleChunkLoadingAt(int x, int z)
    {
        if (x < 0 || x >= DIAMETER || z < 0 || z >= DIAMETER
              || x == MAX_LOAD_RADIUS && z == MAX_LOAD_RADIUS)
        {
            return false;
        }
        loadingChunks[x][z] = !loadingChunks[x][z];
        chunksLoaded += loadingChunks[x][z] ? 1 : -1;
        markDirty();
        if (active && chunkTicket != null)
        {
            applyForcedChunks(chunkTicket);
        }
        sendUpdatePacket();
        return true;
    }

    private void ensureChunkTicket()
    {
        if (chunkTicket != null)
        {
            return;
        }
        Ticket ticket = ForgeChunkManager.requestTicket(
              MekanismUltimate.instance, worldObj, Type.NORMAL);
        if (ticket != null)
        {
            forceChunks(ticket);
        }
    }

    @Override
    public void forceChunks(Ticket ticket)
    {
        if (ticket == null)
        {
            return;
        }
        if (chunkTicket != null && chunkTicket != ticket)
        {
            releaseChunks();
        }
        chunkTicket = ticket;
        ticket.getModData().setInteger("xCoord", xCoord);
        ticket.getModData().setInteger("yCoord", yCoord);
        ticket.getModData().setInteger("zCoord", zCoord);
        if (active && UltimateConfig.allowDimensionalStabilizerChunkloading)
        {
            applyForcedChunks(ticket);
        }
        else
        {
            releaseChunks();
        }
    }

    private void applyForcedChunks(Ticket ticket)
    {
        for (ChunkCoordIntPair forced : new ArrayList<ChunkCoordIntPair>(
              ticket.getChunkList()))
        {
            ForgeChunkManager.unforceChunk(ticket, forced);
        }
        int centerX = xCoord >> 4;
        int centerZ = zCoord >> 4;
        int remaining = Math.max(0, ticket.getChunkListDepth());
        for (int x = 0; x < DIAMETER && remaining > 0; x++)
        {
            for (int z = 0; z < DIAMETER && remaining > 0; z++)
            {
                if (loadingChunks[x][z])
                {
                    ForgeChunkManager.forceChunk(ticket,
                          new ChunkCoordIntPair(centerX + x
                                - MAX_LOAD_RADIUS, centerZ + z
                                - MAX_LOAD_RADIUS));
                    remaining--;
                }
            }
        }
    }

    public Set<ChunkCoordIntPair> getSelectedChunks()
    {
        Set<ChunkCoordIntPair> selected =
              new HashSet<ChunkCoordIntPair>();
        int centerX = xCoord >> 4;
        int centerZ = zCoord >> 4;
        for (int x = 0; x < DIAMETER; x++)
        {
            for (int z = 0; z < DIAMETER; z++)
            {
                if (loadingChunks[x][z])
                {
                    selected.add(new ChunkCoordIntPair(centerX + x
                          - MAX_LOAD_RADIUS, centerZ + z
                          - MAX_LOAD_RADIUS));
                }
            }
        }
        return selected;
    }

    public void releaseChunks()
    {
        if (chunkTicket != null)
        {
            ForgeChunkManager.releaseTicket(chunkTicket);
            chunkTicket = null;
        }
    }

    @Override
    public void invalidate()
    {
        if (worldObj != null && !worldObj.isRemote)
        {
            releaseChunks();
        }
        super.invalidate();
    }

    @Override
    public void recalculateUpgradables(Upgrade upgrade)
    {
        super.recalculateUpgradables(upgrade);
        if (upgrade == Upgrade.ENERGY)
        {
            updateConfiguredEnergyValues();
        }
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
    public EnumSet<ForgeDirection> getConsumingSides()
    {
        return EnumSet.allOf(ForgeDirection.class);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return stack != null && (slot == ENERGY_SLOT
              ? ChargeUtils.canBeDischarged(stack)
              : slot == UPGRADE_SLOT
                    && stack.getItem() instanceof IUpgradeItem
                    && ((IUpgradeItem)stack.getItem()).getUpgradeType(stack)
                          == Upgrade.ENERGY);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return slot == ENERGY_SLOT && stack != null
              && ChargeUtils.canBeOutputted(stack, false);
    }

    @Override
    public boolean canSetFacing(int direction)
    {
        return direction >= 2 && direction <= 5;
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
        markDirty();
    }

    @Override
    public boolean canPulse()
    {
        return false;
    }

    @Override
    public boolean getActive()
    {
        return active;
    }

    @Override
    public void setActive(boolean value)
    {
        if (active == value)
        {
            return;
        }
        active = value;
        clientActive = value;
        markDirty();
        sendUpdatePacket();
        MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
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

    private void sendUpdatePacket()
    {
        Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(
              Coord4D.get(this), getNetworkedData(new ArrayList())),
              new Range4D(Coord4D.get(this)));
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        active = data.getBoolean("isActive");
        clientActive = active;
        int control = data.getInteger("controlType");
        controlType = control >= 0 && control < RedstoneControl.values().length
              ? RedstoneControl.values()[control]
              : RedstoneControl.DISABLED;
        readChunkSelection(data.getByteArray("stabilizedChunks"));
    }

    private void readChunkSelection(byte[] configured)
    {
        chunksLoaded = 0;
        for (int x = 0; x < DIAMETER; x++)
        {
            for (int z = 0; z < DIAMETER; z++)
            {
                int index = x * DIAMETER + z;
                boolean center = x == MAX_LOAD_RADIUS
                      && z == MAX_LOAD_RADIUS;
                loadingChunks[x][z] = center
                      || configured.length == DIAMETER * DIAMETER
                            && configured[index] != 0;
                if (loadingChunks[x][z])
                {
                    chunksLoaded++;
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("isActive", active);
        data.setInteger("controlType", controlType.ordinal());
        byte[] configured = new byte[DIAMETER * DIAMETER];
        for (int x = 0; x < DIAMETER; x++)
        {
            for (int z = 0; z < DIAMETER; z++)
            {
                configured[x * DIAMETER + z] =
                      (byte)(loadingChunks[x][z] ? 1 : 0);
            }
        }
        data.setByteArray("stabilizedChunks", configured);
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            active = data.readBoolean();
            clientActive = active;
            clientEnergyUsed = data.readDouble();
            chunksLoaded = 0;
            for (int x = 0; x < DIAMETER; x++)
            {
                for (int z = 0; z < DIAMETER; z++)
                {
                    loadingChunks[x][z] = data.readBoolean();
                    if (loadingChunks[x][z])
                    {
                        chunksLoaded++;
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(active);
        data.add(clientEnergyUsed);
        for (int x = 0; x < DIAMETER; x++)
        {
            for (int z = 0; z < DIAMETER; z++)
            {
                data.add(loadingChunks[x][z]);
            }
        }
        return data;
    }

    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("tile.DimensionalStabilizer.name");
    }
}
