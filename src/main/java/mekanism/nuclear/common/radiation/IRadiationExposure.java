package mekanism.nuclear.common.radiation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/** Persistent accumulated radiation dose for a player, measured in sieverts. */
public interface IRadiationExposure extends INBTSerializable<NBTTagCompound> {

    double getRadiation();

    void setRadiation(double radiation);

    void radiate(double magnitude);

    void decay(double decayRate);
}
