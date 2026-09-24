package mekanism.ultimate.common.content.machine;

import java.util.Objects;
import javax.annotation.Nullable;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTypeResolverCE;
import mekanism.ultimate.api.recipe.IRecipeManagerCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Non-registered single-operation machine core used to validate the recipe
 * layer and later reused by Nuclear's Isotopic Centrifuge.
 *
 * <p>The tanks are private and every mutation is synchronized on the processor,
 * allowing simulation and commit to remain one atomic server-side operation.</p>
 */
public final class ChemicalToChemicalRecipeProcessorCE {

    private static final String NBT_INPUT = "InputTank";
    private static final String NBT_OUTPUT = "OutputTank";

    private final IRecipeManagerCE<IChemicalStackCE, ChemicalToChemicalRecipeCE> recipes;
    private final LongChemicalTank inputTank;
    private final LongChemicalTank outputTank;

    public ChemicalToChemicalRecipeProcessorCE(IRecipeManagerCE<IChemicalStackCE, ChemicalToChemicalRecipeCE> recipes,
          long inputCapacity, long outputCapacity) {
        this.recipes = Objects.requireNonNull(recipes, "recipes");
        this.inputTank = new LongChemicalTank(inputCapacity);
        this.outputTank = new LongChemicalTank(outputCapacity);
    }

    public synchronized long insertInput(IChemicalStackCE stack, Action action) {
        return inputTank.insert(stack, action);
    }

    @Nullable
    public synchronized ChemicalStackCE extractOutput(long amount, Action action) {
        return outputTank.extract(amount, action);
    }

    @Nullable
    public synchronized IChemicalStackCE getInput() {
        return inputTank.getStack();
    }

    @Nullable
    public synchronized IChemicalStackCE getOutput() {
        return outputTank.getStack();
    }

    public synchronized long getInputStored() {
        return inputTank.getStored();
    }

    public synchronized long getOutputStored() {
        return outputTank.getStored();
    }

    /**
     * Exposes the authoritative long-backed input tank to machine integrations.
     * Mutating operations remain atomic at the tank level; recipes should still
     * be committed through {@link #processOnce()}.
     */
    public LongChemicalTank getInputTank() {
        return inputTank;
    }

    /** Exposes the authoritative long-backed output tank to API adapters. */
    public LongChemicalTank getOutputTank() {
        return outputTank;
    }

    public synchronized RecipeProcessResultCE processOnce() {
        IChemicalStackCE input = inputTank.getStack();
        if (input == null || input.isEmpty()) {
            return RecipeProcessResultCE.NO_INPUT;
        }

        ChemicalToChemicalRecipeCE recipe = recipes.findFirst(input);
        if (recipe == null) {
            return RecipeProcessResultCE.NO_RECIPE;
        }
        long required = recipe.getInput().getAmount();
        if (!recipe.getInput().hasRequiredAmount(input)) {
            return RecipeProcessResultCE.INPUT_TOO_SMALL;
        }
        ChemicalStackCE produced = recipe.getOutput();
        if (outputTank.insert(produced, Action.SIMULATE) != produced.getAmount()) {
            return RecipeProcessResultCE.OUTPUT_BLOCKED;
        }
        IChemicalStackCE simulatedExtraction = inputTank.extract(required, Action.SIMULATE);
        if (simulatedExtraction == null || simulatedExtraction.getAmount() != required) {
            return RecipeProcessResultCE.INPUT_TOO_SMALL;
        }

        IChemicalStackCE extracted = inputTank.extract(required, Action.EXECUTE);
        long inserted = outputTank.insert(produced, Action.EXECUTE);
        if (extracted == null || extracted.getAmount() != required || inserted != produced.getAmount()) {
            throw new IllegalStateException("Chemical recipe commit diverged from successful simulation");
        }
        return RecipeProcessResultCE.PROCESSED;
    }

    public synchronized void writeToNBT(NBTTagCompound data) {
        Objects.requireNonNull(data, "data");
        NBTTagCompound inputData = new NBTTagCompound();
        inputTank.writeToNBT(inputData);
        data.setTag(NBT_INPUT, inputData);
        NBTTagCompound outputData = new NBTTagCompound();
        outputTank.writeToNBT(outputData);
        data.setTag(NBT_OUTPUT, outputData);
    }

    public synchronized void readFromNBT(NBTTagCompound data, IChemicalTypeResolverCE resolver) {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(resolver, "resolver");
        inputTank.readFromNBT(data.getCompoundTag(NBT_INPUT), resolver);
        outputTank.readFromNBT(data.getCompoundTag(NBT_OUTPUT), resolver);
    }
}
