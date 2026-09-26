package mekanism.common.recipe.machines;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.outputs.GasOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class DissolutionRecipe extends MachineRecipe<ItemStackInput, GasOutput, DissolutionRecipe> {

    private final int baseChemicalUsage;

    public DissolutionRecipe(ItemStackInput input, GasOutput output) {
        this(input, output, 1);
    }

    public DissolutionRecipe(ItemStackInput input, GasOutput output, int baseChemicalUsage) {
        super(input, output);
        if (baseChemicalUsage < 1) {
            throw new IllegalArgumentException("Dissolution chemical usage must be positive");
        }
        this.baseChemicalUsage = baseChemicalUsage;
    }

    public DissolutionRecipe(ItemStack input, GasStack output) {
        this(input, output, 1);
    }

    public DissolutionRecipe(ItemStack input, GasStack output, int baseChemicalUsage) {
        this(new ItemStackInput(input), new GasOutput(output), baseChemicalUsage);
    }

    public int getBaseChemicalUsage() {
        return baseChemicalUsage;
    }

    public int scaleChemicalUsage(int machineUsage) {
        long scaled = (long) Math.max(1, machineUsage) * baseChemicalUsage;
        return scaled > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) scaled;
    }

    public boolean canOperate(NonNullList<ItemStack> inventory, GasTank outputTank) {
        return getInput().useItemStackFromInventory(inventory, 1, false) && getOutput().applyOutputs(outputTank, false, 1);
    }

    public void operate(NonNullList<ItemStack> inventory, GasTank outputTank) {
        if (getInput().useItemStackFromInventory(inventory, 1, true)) {
            getOutput().applyOutputs(outputTank, true, 1);
        }
    }

    @Override
    public DissolutionRecipe copy() {
        return new DissolutionRecipe(getInput().copy(), getOutput().copy(), baseChemicalUsage);
    }
}
