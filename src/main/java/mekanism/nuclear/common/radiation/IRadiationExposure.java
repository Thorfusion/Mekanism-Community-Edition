package mekanism.nuclear.common.radiation;

import net.minecraft.nbt.NBTTagCompound;

/** Persistent accumulated radiation dose for a player, measured in sieverts. */
public interface IRadiationExposure
{
    double getRadiation();

    void setRadiation(double radiation);

    void radiate(double magnitude);

    void decay(double decayRate);

    NBTTagCompound serializeNBT();

    void deserializeNBT(NBTTagCompound data);
}
