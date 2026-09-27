package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import mekanism.api.TileNetworkList;
import mekanism.common.Upgrade;
import mekanism.common.Mekanism;
import mekanism.common.chunkloading.IChunkLoader;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.component.TileComponentChunkLoader;
import mekanism.common.tile.prefab.TileEntityMachine;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

/** Energy-backed, selectable 5x5 chunk loader matching the stable machine. */
public final class TileEntityDimensionalStabilizer extends TileEntityMachine implements IChunkLoader {

    public static final int MAX_LOAD_RADIUS = 2;
    public static final int DIAMETER = MAX_LOAD_RADIUS * 2 + 1;
    public static final int ENERGY_SLOT = 0;
    public static final int UPGRADE_SLOT = 1;
    public static final double BASE_USAGE_PER_CHUNK = 5_000D;
    private static final int[] SLOTS = {ENERGY_SLOT, UPGRADE_SLOT};

    private final boolean[][] loadingChunks = new boolean[DIAMETER][DIAMETER];
    private final TileComponentChunkLoader chunkLoader;
    private int chunksLoaded = 1;
    public double clientEnergyUsed;

    public TileEntityDimensionalStabilizer() {
        super("null", "DimensionalStabilizer", 40_000D, BASE_USAGE_PER_CHUNK, UPGRADE_SLOT);
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        loadingChunks[MAX_LOAD_RADIUS][MAX_LOAD_RADIUS] = true;
        upgradeComponent.setSupported(Upgrade.SPEED, false);
        upgradeComponent.setSupported(Upgrade.MUFFLING, false);
        chunkLoader = new TileComponentChunkLoader(this) {
            @Override
            public boolean canOperate() {
                return MekanismConfig.current().general.allowChunkloading.val()
                      && TileEntityDimensionalStabilizer.this.getActive();
            }
        };
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            return;
        }
        ChargeUtils.discharge(ENERGY_SLOT, this);
        double required = getEnergyUsage();
        boolean active = MekanismConfig.current().general.allowChunkloading.val()
              && MekanismUtils.canFunction(this) && getEnergy() >= required;
        setActive(active);
        if (active) {
            setEnergy(getEnergy() - required);
            clientEnergyUsed = required;
        } else {
            clientEnergyUsed = 0;
        }
        prevEnergy = getEnergy();
    }

    public double getEnergyUsage() {
        return energyPerTick * chunksLoaded;
    }

    public int getChunksLoaded() {
        return chunksLoaded;
    }

    public boolean isChunkLoadingAt(int x, int z) {
        return x >= 0 && x < DIAMETER && z >= 0 && z < DIAMETER && loadingChunks[x][z];
    }

    public void toggleChunkLoadingAt(int x, int z) {
        if (x < 0 || x >= DIAMETER || z < 0 || z >= DIAMETER
              || x == MAX_LOAD_RADIUS && z == MAX_LOAD_RADIUS) {
            return;
        }
        setChunkLoadingAt(x, z, !loadingChunks[x][z]);
    }

    private void setChunkLoadingAt(int x, int z, boolean load) {
        if (loadingChunks[x][z] == load) {
            return;
        }
        loadingChunks[x][z] = load;
        chunksLoaded += load ? 1 : -1;
        markDirty();
        chunkLoader.refreshChunkSet();
        Mekanism.packetHandler.sendUpdatePacket(this);
    }

    @Override
    public TileComponentChunkLoader getChunkLoader() {
        return chunkLoader;
    }

    @Override
    public Set<ChunkPos> getChunkSet() {
        Set<ChunkPos> chunks = new HashSet<>();
        int centerX = pos.getX() >> 4;
        int centerZ = pos.getZ() >> 4;
        for (int x = 0; x < DIAMETER; x++) {
            for (int z = 0; z < DIAMETER; z++) {
                if (loadingChunks[x][z]) {
                    chunks.add(new ChunkPos(centerX + x - MAX_LOAD_RADIUS,
                          centerZ + z - MAX_LOAD_RADIUS));
                }
            }
        }
        return chunks;
    }

    @Override
    public void recalculateUpgradables(Upgrade upgrade) {
        super.recalculateUpgradables(upgrade);
        if (upgrade == Upgrade.ENERGY) {
            energyPerTick = MekanismUtils.getEnergyPerTick(this, BASE_USAGE_PER_CHUNK);
        }
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            int type = data.readInt();
            if (type == 0) {
                toggleChunkLoadingAt(data.readInt(), data.readInt());
            }
            return;
        }
        super.handlePacketData(data);
        clientEnergyUsed = data.readDouble();
        chunksLoaded = 0;
        for (int x = 0; x < DIAMETER; x++) {
            for (int z = 0; z < DIAMETER; z++) {
                loadingChunks[x][z] = data.readBoolean();
                if (loadingChunks[x][z]) chunksLoaded++;
            }
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(clientEnergyUsed);
        for (int x = 0; x < DIAMETER; x++) {
            for (int z = 0; z < DIAMETER; z++) data.add(loadingChunks[x][z]);
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        byte[] configured = data.getByteArray("stabilizedChunks");
        chunksLoaded = 0;
        for (int x = 0; x < DIAMETER; x++) {
            for (int z = 0; z < DIAMETER; z++) {
                int index = x * DIAMETER + z;
                boolean center = x == MAX_LOAD_RADIUS && z == MAX_LOAD_RADIUS;
                loadingChunks[x][z] = center || configured.length == DIAMETER * DIAMETER
                      && configured[index] != 0;
                if (loadingChunks[x][z]) chunksLoaded++;
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        byte[] configured = new byte[DIAMETER * DIAMETER];
        for (int x = 0; x < DIAMETER; x++) {
            for (int z = 0; z < DIAMETER; z++) {
                configured[x * DIAMETER + z] = (byte) (loadingChunks[x][z] ? 1 : 0);
            }
        }
        data.setByteArray("stabilizedChunks", configured);
        return data;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return slot == ENERGY_SLOT ? ChargeUtils.canBeDischarged(stack)
              : slot == UPGRADE_SLOT && stack.getItem() instanceof mekanism.common.base.IUpgradeItem;
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side) {
        return slot == ENERGY_SLOT && ChargeUtils.canBeOutputted(stack, false);
    }

    @Nonnull
    @Override
    public String getName() {
        return LangUtils.localize("tile.DimensionalStabilizer.name");
    }
}
