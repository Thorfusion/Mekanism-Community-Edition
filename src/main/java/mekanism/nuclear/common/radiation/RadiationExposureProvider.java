package mekanism.nuclear.common.radiation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/** Serializable player capability provider for accumulated radiation. */
public final class RadiationExposureProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final IRadiationExposure exposure = new RadiationExposure();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        return capability == RadiationCapabilities.EXPOSURE_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
        return capability == RadiationCapabilities.EXPOSURE_CAPABILITY
              ? RadiationCapabilities.EXPOSURE_CAPABILITY.cast(exposure) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return exposure.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound data) {
        exposure.deserializeNBT(data);
    }
}
