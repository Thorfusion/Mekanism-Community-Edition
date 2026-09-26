package mekanism.ultimate.common.capability;

import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/** Forge capability bridge for the Ultimate chemical facade. */
public final class UltimateChemicalCapabilities {

    @CapabilityInject(IChemicalHandlerCE.class)
    public static Capability<IChemicalHandlerCE> CHEMICAL_HANDLER_CAPABILITY = null;

    private UltimateChemicalCapabilities() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IChemicalHandlerCE.class,
              new Capability.IStorage<IChemicalHandlerCE>() {
                  @Override
                  public NBTBase writeNBT(Capability<IChemicalHandlerCE> capability,
                        IChemicalHandlerCE instance, EnumFacing side) {
                      return null;
                  }

                  @Override
                  public void readNBT(Capability<IChemicalHandlerCE> capability,
                        IChemicalHandlerCE instance, EnumFacing side, NBTBase nbt) {
                  }
              }, EmptyChemicalHandler::new);
    }

    private static final class EmptyChemicalHandler implements IChemicalHandlerCE {

        @Override
        public int getChemicalTankCount(EnumFacing side) {
            return 0;
        }

        @Override
        public IChemicalTankCE getChemicalTank(int tank, EnumFacing side) {
            throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
        }

        @Override
        public boolean canInsertChemical(int tank, IChemicalTypeCE type, EnumFacing side) {
            return false;
        }

        @Override
        public boolean canExtractChemical(int tank, IChemicalTypeCE type, EnumFacing side) {
            return false;
        }
    }
}
