package mekanism.nuclear.common.radiation;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/** Nuclear-owned capability registration for persistent entity radiation dose. */
public final class RadiationCapabilities {

    @CapabilityInject(IRadiationExposure.class)
    public static Capability<IRadiationExposure> EXPOSURE_CAPABILITY = null;

    private RadiationCapabilities() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IRadiationExposure.class,
              new Capability.IStorage<IRadiationExposure>() {
                  @Override
                  public NBTBase writeNBT(Capability<IRadiationExposure> capability,
                        IRadiationExposure instance, EnumFacing side) {
                      return instance.serializeNBT();
                  }

                  @Override
                  public void readNBT(Capability<IRadiationExposure> capability,
                        IRadiationExposure instance, EnumFacing side, NBTBase nbt) {
                      if (nbt instanceof NBTTagCompound) {
                          instance.deserializeNBT((NBTTagCompound) nbt);
                      }
                  }
              }, RadiationExposure::new);
    }
}
