package mekanism.common.content.boiler;

import mekanism.api.gas.GasStack;
import mekanism.common.multiblock.MultiblockCache;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class BoilerCache extends MultiblockCache<SynchronizedBoilerData> {

    public FluidStack water;
    public FluidStack steam;
    public GasStack superheatedCoolant;
    public GasStack cooledCoolant;
    public double temperature;

    @Override
    public void apply(SynchronizedBoilerData data) {
        data.waterStored = water;
        data.steamStored = steam;
        data.superheatedCoolantTank.setGas(copy(superheatedCoolant));
        data.cooledCoolantTank.setGas(copy(cooledCoolant));
        data.temperature = temperature;
    }

    @Override
    public void sync(SynchronizedBoilerData data) {
        water = data.waterStored;
        steam = data.steamStored;
        superheatedCoolant = copy(data.superheatedCoolantTank.getGas());
        cooledCoolant = copy(data.cooledCoolantTank.getGas());
        temperature = data.temperature;
    }

    @Override
    public void load(NBTTagCompound nbtTags) {
        if (nbtTags.hasKey("cachedWater")) {
            water = FluidStack.loadFluidStackFromNBT(nbtTags.getCompoundTag("cachedWater"));
        }
        if (nbtTags.hasKey("cachedSteam")) {
            steam = FluidStack.loadFluidStackFromNBT(nbtTags.getCompoundTag("cachedSteam"));
        }
        if (nbtTags.hasKey("cachedSuperheatedCoolant")) {
            superheatedCoolant = GasStack.readFromNBT(nbtTags.getCompoundTag("cachedSuperheatedCoolant"));
        }
        if (nbtTags.hasKey("cachedCooledCoolant")) {
            cooledCoolant = GasStack.readFromNBT(nbtTags.getCompoundTag("cachedCooledCoolant"));
        }
        temperature = nbtTags.getDouble("temperature");
    }

    @Override
    public void save(NBTTagCompound nbtTags) {
        if (water != null) {
            nbtTags.setTag("cachedWater", water.writeToNBT(new NBTTagCompound()));
        }
        if (steam != null) {
            nbtTags.setTag("cachedSteam", steam.writeToNBT(new NBTTagCompound()));
        }
        if (superheatedCoolant != null) {
            nbtTags.setTag("cachedSuperheatedCoolant", superheatedCoolant.write(new NBTTagCompound()));
        }
        if (cooledCoolant != null) {
            nbtTags.setTag("cachedCooledCoolant", cooledCoolant.write(new NBTTagCompound()));
        }
        nbtTags.setDouble("temperature", temperature);
    }

    private static GasStack copy(GasStack stack) {
        return stack == null ? null : stack.copy();
    }
}
