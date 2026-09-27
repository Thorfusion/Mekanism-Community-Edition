package mekanism.ultimate.common.tile;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.common.Upgrade;
import mekanism.common.Upgrade.IUpgradeInfoHandler;
import mekanism.api.TileNetworkList;
import mekanism.common.PacketHandler;
import mekanism.common.tile.prefab.TileEntityMachine;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import io.netty.buffer.ByteBuf;
import mekanism.ultimate.common.capability.UltimateChemicalCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public abstract class TileEntityPigmentMachine extends TileEntityMachine
      implements IChemicalHandlerCE, IUpgradeInfoHandler {

    public static final long TANK_CAPACITY = 20_000;
    public double clientEnergyUsed;
    protected int operatingTicks;
    private final String translationKey;

    protected TileEntityPigmentMachine(String name, String translationKey, double capacity,
          double usage, int upgradeSlot) {
        // Ultimate has no 1.12 machine-loop sounds for these modern machines yet.
        // A null sound avoids asking the Core sound handler for a nonexistent key.
        super("null", name, capacity, usage, upgradeSlot);
        this.translationKey = translationKey;
        upgradeComponent.setSupported(Upgrade.SPEED);
        upgradeComponent.setSupported(Upgrade.ENERGY);
        upgradeComponent.setSupported(Upgrade.MUFFLING);
    }

    protected int ticksRequired(int baseTicks) {
        return Math.max(1, baseTicks >> upgradeComponent.getUpgrades(Upgrade.SPEED));
    }

    public double getProgress(int baseTicks) {
        return (double) operatingTicks / ticksRequired(baseTicks);
    }

    protected static void addTankSnapshot(TileNetworkList data, IChemicalTankCE tank) {
        IChemicalStackCE stack = tank.getStack();
        data.add(stack == null ? -1 : stack.getType().getKind().ordinal());
        data.add(stack == null ? "" : stack.getType().getRegistryName());
        data.add(stack == null ? 0L : stack.getAmount());
    }

    protected static void readTankSnapshot(ByteBuf data, IChemicalTankCE tank) {
        int kindIndex = data.readInt();
        String name = PacketHandler.readString(data);
        long amount = data.readLong();
        tank.extract(tank.getStored(), mekanism.ultimate.api.Action.EXECUTE);
        mekanism.ultimate.api.chemical.ChemicalKind[] kinds =
              mekanism.ultimate.api.chemical.ChemicalKind.values();
        if (kindIndex >= 0 && kindIndex < kinds.length && amount > 0) {
            mekanism.ultimate.api.chemical.IChemicalTypeCE type =
                  UltimateChemicalRegistry.INSTANCE.resolve(kinds[kindIndex], name);
            if (type != null) {
                tank.insert(new ChemicalStackCE(type, amount), mekanism.ultimate.api.Action.EXECUTE);
            }
        }
    }

    @Override
    public void recalculateUpgradables(Upgrade upgrade) {
        super.recalculateUpgradables(upgrade);
        if (upgrade == Upgrade.SPEED || upgrade == Upgrade.ENERGY) {
            energyPerTick = MekanismUtils.getEnergyPerTick(this, BASE_ENERGY_PER_TICK);
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        return capability == UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY
              || super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if (capability == UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY) {
            return UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public List<String> getInfo(Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? upgrade.getExpScaledInfo(this) : upgrade.getMultScaledInfo(this);
    }

    @Nonnull
    @Override
    public String getName() {
        return LangUtils.localize(translationKey);
    }
}
