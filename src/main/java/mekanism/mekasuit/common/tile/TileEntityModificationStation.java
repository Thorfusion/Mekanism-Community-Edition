package mekanism.mekasuit.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.EnumSet;
import mekanism.api.Coord4D;
import mekanism.common.MekanismBlocks;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.ModificationStationOperations;
import mekanism.mekasuit.common.item.ItemMekaModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Energy-backed installer for MekaSuit units. */
public final class TileEntityModificationStation extends TileEntityElectricBlock
      implements ISecurityTile, IBoundingBlock
{
    public static final int MODULE_SLOT = 0;
    public static final int CONTAINER_SLOT = 1;
    public static final int ENERGY_SLOT = 2;
    public static final int TICKS_REQUIRED = 10;

    private static final int[] AUTOMATION_SLOTS = {ENERGY_SLOT};

    private final TileComponentSecurity securityComponent =
          new TileComponentSecurity(this);
    private int operatingTicks;
    private boolean checkedBoundingBlocks;
    public double clientEnergyUsed;

    public TileEntityModificationStation()
    {
        super("ModificationStation",
              MekaSuitConfig.modificationStationCapacity);
        inventory = new ItemStack[3];
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (worldObj.isRemote)
        {
            return;
        }

        if (!checkedBoundingBlocks)
        {
            ensureBoundingBlocks();
            checkedBoundingBlocks = true;
        }

        maxEnergy = MekaSuitConfig.modificationStationCapacity;
        ChargeUtils.discharge(ENERGY_SLOT, this);
        ItemStack moduleStack = inventory[MODULE_SLOT];
        ItemStack host = inventory[CONTAINER_SLOT];
        boolean canInstall = ModificationStationOperations
              .getInstallableCount(host, moduleStack) > 0;
        if (canInstall && getEnergy() >= MekaSuitConfig.modificationStationUsage)
        {
            setEnergy(getEnergy() - MekaSuitConfig.modificationStationUsage);
            clientEnergyUsed = MekaSuitConfig.modificationStationUsage;
            if (++operatingTicks >= TICKS_REQUIRED)
            {
                int installed = ModificationStationOperations.install(host,
                      moduleStack);
                if (installed > 0)
                {
                    moduleStack.stackSize -= installed;
                    if (moduleStack.stackSize <= 0)
                    {
                        inventory[MODULE_SLOT] = null;
                    }
                    markDirty();
                }
                operatingTicks = 0;
            }
        }
        else
        {
            operatingTicks = 0;
            clientEnergyUsed = 0;
        }
    }

    public ItemStack getContainerStack()
    {
        return inventory[CONTAINER_SLOT];
    }

    public double getScaledProgress()
    {
        return (double)operatingTicks / TICKS_REQUIRED;
    }

    public int getOperatingTicks()
    {
        return operatingTicks;
    }

    public int getRedstoneLevel()
    {
        return Container.calcRedstoneFromInventory(this);
    }

    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("tile.ModificationStation.name");
    }

    @Override
    public boolean canSetFacing(int direction)
    {
        return direction == ForgeDirection.NORTH.ordinal()
              || direction == ForgeDirection.SOUTH.ordinal()
              || direction == ForgeDirection.WEST.ordinal()
              || direction == ForgeDirection.EAST.ordinal();
    }

    /**
     * The JSON model occupies two horizontal cells and two vertical cells.
     * Real proxy blocks make every visible part selectable in 1.7.10, whose
     * ray tracer otherwise ignores bounds extending into an empty neighbor.
     */
    @Override
    public void onPlace()
    {
        if (worldObj == null || worldObj.isRemote)
        {
            return;
        }
        ensureBoundingBlocks();
        checkedBoundingBlocks = true;
    }

    @Override
    public void onBreak()
    {
        removeBoundingBlocks();
    }

    public boolean canFormBoundingBlocks(int candidateFacing)
    {
        return canFormBoundingBlocks(worldObj, xCoord, yCoord, zCoord,
              candidateFacing, true);
    }

    public static boolean canFormBoundingBlocks(World world, int x, int y,
          int z, int candidateFacing, boolean allowOwned)
    {
        if (world == null || y < 0 || y + 1 >= world.getHeight())
        {
            return false;
        }
        int[][] parts = getBoundingOffsets(candidateFacing);
        for (int[] part : parts)
        {
            int partX = x + part[0];
            int partY = y + part[1];
            int partZ = z + part[2];
            if (allowOwned && isOwnedBoundingBlock(world, partX, partY,
                  partZ, x, y, z))
            {
                continue;
            }
            Block block = world.getBlock(partX, partY, partZ);
            if (block != Blocks.air
                  && !block.isReplaceable(world, partX, partY, partZ))
            {
                return false;
            }
        }
        return true;
    }

    public void removeBoundingBlocks()
    {
        if (worldObj == null || worldObj.isRemote)
        {
            return;
        }
        for (int[] part : getBoundingOffsets(facing))
        {
            int partX = xCoord + part[0];
            int partY = yCoord + part[1];
            int partZ = zCoord + part[2];
            if (isOwnedBoundingBlock(worldObj, partX, partY, partZ, xCoord,
                  yCoord, zCoord))
            {
                worldObj.setBlockToAir(partX, partY, partZ);
            }
        }
        checkedBoundingBlocks = false;
    }

    private void ensureBoundingBlocks()
    {
        Coord4D main = Coord4D.get(this);
        for (int[] part : getBoundingOffsets(facing))
        {
            int partX = xCoord + part[0];
            int partY = yCoord + part[1];
            int partZ = zCoord + part[2];
            if (isOwnedBoundingBlock(worldObj, partX, partY, partZ, xCoord,
                  yCoord, zCoord))
            {
                continue;
            }
            Block block = worldObj.getBlock(partX, partY, partZ);
            if (block == Blocks.air
                  || block.isReplaceable(worldObj, partX, partY, partZ))
            {
                MekanismUtils.makeBoundingBlock(worldObj,
                      new Coord4D(partX, partY, partZ,
                            worldObj.provider.dimensionId), main);
            }
        }
    }

    static int[][] getBoundingOffsets(int stationFacing)
    {
        int sideX = stationFacing == 2 ? -1 : stationFacing == 3 ? 1 : 0;
        int sideZ = stationFacing == 5 ? -1 : stationFacing == 4 ? 1 : 0;
        return new int[][] {
              {sideX, 0, sideZ},
              {0, 1, 0},
              {sideX, 1, sideZ}
        };
    }

    private static boolean isOwnedBoundingBlock(World world, int x, int y,
          int z, int mainX, int mainY, int mainZ)
    {
        if (world.getBlock(x, y, z) != MekanismBlocks.BoundingBlock
              || !(world.getTileEntity(x, y, z)
                    instanceof TileEntityBoundingBlock))
        {
            return false;
        }
        TileEntityBoundingBlock bounding = (TileEntityBoundingBlock)world
              .getTileEntity(x, y, z);
        return bounding.receivedCoords && bounding.mainX == mainX
              && bounding.mainY == mainY && bounding.mainZ == mainZ;
    }

    @Override
    public EnumSet<ForgeDirection> getConsumingSides()
    {
        ForgeDirection front = ForgeDirection.getOrientation(facing);
        return front == ForgeDirection.UNKNOWN
              ? EnumSet.allOf(ForgeDirection.class)
              : EnumSet.of(front.getOpposite());
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return AUTOMATION_SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }
        if (slot == MODULE_SLOT)
        {
            return stack.getItem() instanceof ItemMekaModule;
        }
        if (slot == CONTAINER_SLOT)
        {
            return stack.getItem() instanceof IModuleContainerItem;
        }
        return slot == ENERGY_SLOT && ChargeUtils.canBeDischarged(stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return slot == ENERGY_SLOT && stack != null
              && ChargeUtils.canBeOutputted(stack, false);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        operatingTicks = Math.max(0, Math.min(TICKS_REQUIRED - 1,
              data.getInteger("operatingTicks")));
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setInteger("operatingTicks", operatingTicks);
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            operatingTicks = data.readInt();
            clientEnergyUsed = data.readDouble();
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(operatingTicks);
        data.add(clientEnergyUsed);
        return data;
    }

    @Override
    public TileComponentSecurity getSecurity()
    {
        return securityComponent;
    }
}
