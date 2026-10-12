package mekanism.ultimate.common.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.client;
import mekanism.api.Pos3D;
import mekanism.api.Range4D;
import mekanism.client.sound.ISoundSource;
import mekanism.common.Mekanism;
import mekanism.common.base.IHasSound;
import mekanism.common.base.SoundWrapper;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/** Redstone-controlled alarm with client-local looping audio. */
public final class TileEntityIndustrialAlarm extends TileEntityBasicBlock
      implements IHasSound, ISoundSource
{
    private boolean active;

    @SideOnly(Side.CLIENT)
    private SoundWrapper sound;

    @Override
    public void onUpdate()
    {
        if (worldObj.isRemote)
        {
            updateSound();
        }
        else if (active != isPowered())
        {
            setActive(isPowered());
        }
    }

    @Override
    public void onPowerChange()
    {
        if (worldObj != null && !worldObj.isRemote)
        {
            setActive(isPowered());
        }
    }

    public boolean isActive()
    {
        return active;
    }

    private void setActive(boolean value)
    {
        if (active == value)
        {
            return;
        }
        active = value;
        markDirty();
        MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
        worldObj.updateLightByType(net.minecraft.world.EnumSkyBlock.Block,
              xCoord, yCoord, zCoord);
        Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(
              Coord4D.get(this), getNetworkedData(new ArrayList())),
              new Range4D(Coord4D.get(this)));
    }

    @Override
    public boolean canSetFacing(int direction)
    {
        return direction >= 0 && direction < 6;
    }

    @Override
    public void validate()
    {
        super.validate();
        if (worldObj.isRemote)
        {
            sound = new SoundWrapper(this, this);
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateSound()
    {
        if (sound != null && shouldPlaySound() && sound.canRestart()
              && client.enableMachineSounds)
        {
            sound.reset();
            sound.play();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SoundWrapper getSound()
    {
        return sound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldPlaySound()
    {
        return active && !isInvalid();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getSoundLocation()
    {
        return new ResourceLocation(MekanismUltimate.MODID.toLowerCase(),
              "tile.industrial_alarm");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getVolume()
    {
        return 1F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getPitch()
    {
        return 1F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Pos3D getSoundPosition()
    {
        return new Pos3D(xCoord + 0.5D, yCoord + 0.5D,
              zCoord + 0.5D);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldRepeat()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRepeatDelay()
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AttenuationType getAttenuation()
    {
        return AttenuationType.LINEAR;
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        active = data.getBoolean("active");
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("active", active);
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            active = data.readBoolean();
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(active);
        return data;
    }

    public String getInventoryName()
    {
        return LangUtils.localize("tile.IndustrialAlarm.name");
    }
}
