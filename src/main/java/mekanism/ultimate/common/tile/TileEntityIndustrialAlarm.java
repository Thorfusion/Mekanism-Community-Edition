package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import mekanism.api.TileNetworkList;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.LangUtils;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;

/** Redstone-controlled alarm state; looping audio is delegated to the client proxy. */
public final class TileEntityIndustrialAlarm extends TileEntityBasicBlock {

    private boolean active;

    @Override
    public void onUpdate() {
        if (world.isRemote) {
            MekanismUltimate.proxy.updateIndustrialAlarmSound(pos, active);
        } else if (active != isPowered()) {
            setActive(isPowered());
        }
    }

    @Override
    public void onPowerChange() {
        if (world != null && !world.isRemote) {
            setActive(isPowered());
        }
    }

    public boolean isActive() {
        return active;
    }

    private void setActive(boolean value) {
        if (active != value) {
            active = value;
            markDirty();
            world.checkLight(pos);
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return true;
    }

    @Override
    public void invalidate() {
        if (world != null && world.isRemote) {
            MekanismUltimate.proxy.updateIndustrialAlarmSound(pos, false);
        }
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        if (world != null && world.isRemote) {
            MekanismUltimate.proxy.updateIndustrialAlarmSound(pos, false);
        }
        super.onChunkUnload();
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        active = data.getBoolean("active");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("active", active);
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        super.handlePacketData(data);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            active = data.readBoolean();
            MekanismUltimate.proxy.updateIndustrialAlarmSound(pos, active);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(active);
        return data;
    }

    @Nonnull
    public String getName() {
        return LangUtils.localize("tile.IndustrialAlarm.name");
    }
}
