package mekanism.ultimate.common.content.machine;

import static org.junit.Assert.assertEquals;

import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.recipe.CachedRecipeManagerCE;
import mekanism.ultimate.common.recipe.RecipeManagersCE;
import mekanism.ultimate.common.recipe.key.ChemicalIdentityKeyCE;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.junit.Test;

public class ChemicalToChemicalRecipeProcessorCETest {

    private static final TestChemicalType INPUT = new TestChemicalType("test:input");
    private static final TestChemicalType OUTPUT = new TestChemicalType("test:output");

    @Test
    public void processingIsAtomicWhenInputOrOutputBlocksOperation() {
        ChemicalToChemicalRecipeProcessorCE processor = new ChemicalToChemicalRecipeProcessorCE(manager(10, 3), 100, 5);
        assertEquals(RecipeProcessResultCE.NO_INPUT, processor.processOnce());

        processor.insertInput(new ChemicalStackCE(INPUT, 9), Action.EXECUTE);
        assertEquals(RecipeProcessResultCE.INPUT_TOO_SMALL, processor.processOnce());
        assertEquals(9, processor.getInputStored());
        assertEquals(0, processor.getOutputStored());

        processor.insertInput(new ChemicalStackCE(INPUT, 11), Action.EXECUTE);
        assertEquals(RecipeProcessResultCE.PROCESSED, processor.processOnce());
        assertEquals(10, processor.getInputStored());
        assertEquals(3, processor.getOutputStored());

        assertEquals(RecipeProcessResultCE.OUTPUT_BLOCKED, processor.processOnce());
        assertEquals(10, processor.getInputStored());
        assertEquals(3, processor.getOutputStored());
    }

    @Test
    public void processorPersistsLongAmounts() {
        long largeAmount = (long) Integer.MAX_VALUE + 25_000L;
        ChemicalToChemicalRecipeProcessorCE processor = new ChemicalToChemicalRecipeProcessorCE(manager(largeAmount, 7), largeAmount + 1, 20);
        processor.insertInput(new ChemicalStackCE(INPUT, largeAmount), Action.EXECUTE);
        assertEquals(RecipeProcessResultCE.PROCESSED, processor.processOnce());

        NBTTagCompound data = new NBTTagCompound();
        processor.writeToNBT(data);
        ChemicalToChemicalRecipeProcessorCE loaded = new ChemicalToChemicalRecipeProcessorCE(manager(largeAmount, 7), largeAmount + 1, 20);
        loaded.readFromNBT(data, (kind, name) -> name.equals(INPUT.getRegistryName()) ? INPUT : name.equals(OUTPUT.getRegistryName()) ? OUTPUT : null);

        assertEquals(0, loaded.getInputStored());
        assertEquals(7, loaded.getOutputStored());
    }

    private static CachedRecipeManagerCE<mekanism.ultimate.api.chemical.IChemicalStackCE, ChemicalIdentityKeyCE,
          ChemicalToChemicalRecipeCE> manager(long inputAmount, long outputAmount) {
        CachedRecipeManagerCE<mekanism.ultimate.api.chemical.IChemicalStackCE, ChemicalIdentityKeyCE,
              ChemicalToChemicalRecipeCE> manager = RecipeManagersCE.chemicalToChemical();
        manager.add(new ChemicalToChemicalRecipeCE(new ResourceLocation("test", "conversion"),
              new ChemicalIngredientCE(INPUT, inputAmount), new ChemicalStackCE(OUTPUT, outputAmount)));
        return manager;
    }

    private static final class TestChemicalType implements IChemicalTypeCE {

        private final String name;

        private TestChemicalType(String name) {
            this.name = name;
        }

        @Override
        public String getRegistryName() {
            return name;
        }

        @Override
        public ChemicalKind getKind() {
            return ChemicalKind.GAS;
        }

        @Override
        public boolean isRadioactive() {
            return false;
        }
    }
}
