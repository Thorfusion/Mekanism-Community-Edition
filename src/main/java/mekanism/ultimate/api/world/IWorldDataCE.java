package mekanism.ultimate.api.world;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Versioned persistence contract for Ultimate-owned world data.
 */
public interface IWorldDataCE {

    int getDataVersion();

    void readFromNBT(NBTTagCompound data);

    void writeToNBT(NBTTagCompound data);

    void markDirty();
}
