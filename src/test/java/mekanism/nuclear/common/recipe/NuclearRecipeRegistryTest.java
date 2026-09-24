package mekanism.nuclear.common.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import mekanism.nuclear.common.NuclearChemicals;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.machine.ChemicalToChemicalRecipeProcessorCE;
import mekanism.ultimate.common.content.machine.RecipeProcessResultCE;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import net.minecraft.util.ResourceLocation;
import org.junit.BeforeClass;
import org.junit.Test;

public class NuclearRecipeRegistryTest {

    @BeforeClass
    public static void registerContent() {
        NuclearChemicals.register();
        NuclearRecipeRegistry.registerDefaults();
    }

    @Test
    public void stableModernRecipesKeepExactRatios() {
        assertEquals(2, NuclearRecipeRegistry.CENTRIFUGING.size());

        ChemicalToChemicalRecipeCE fissile = NuclearRecipeRegistry.CENTRIFUGING.get(
              new ResourceLocation("mekanismnuclear", "centrifuging/fissile_fuel"));
        assertNotNull(fissile);
        assertEquals(NuclearChemicals.URANIUM_HEXAFLUORIDE_NAME, fissile.getInput().getType().getRegistryName());
        assertEquals(1, fissile.getInput().getAmount());
        assertEquals(NuclearChemicals.FISSILE_FUEL_NAME, fissile.getOutput().getType().getRegistryName());
        assertEquals(1, fissile.getOutput().getAmount());

        ChemicalToChemicalRecipeCE plutonium = NuclearRecipeRegistry.CENTRIFUGING.get(
              new ResourceLocation("mekanismnuclear", "centrifuging/plutonium"));
        assertNotNull(plutonium);
        assertEquals(NuclearChemicals.NUCLEAR_WASTE_NAME, plutonium.getInput().getType().getRegistryName());
        assertEquals(10, plutonium.getInput().getAmount());
        assertEquals(NuclearChemicals.PLUTONIUM_NAME, plutonium.getOutput().getType().getRegistryName());
        assertEquals(1, plutonium.getOutput().getAmount());
    }

    @Test
    public void registrationIsIdempotentAndInputsAreIndexedByIdentity() {
        NuclearRecipeRegistry.registerDefaults();
        assertEquals(2, NuclearRecipeRegistry.CENTRIFUGING.size());
        assertTrue(NuclearRecipeRegistry.containsInput(NuclearChemicals.URANIUM_HEXAFLUORIDE_NAME));
        assertTrue(NuclearRecipeRegistry.containsInput(NuclearChemicals.NUCLEAR_WASTE_NAME));
        assertFalse(NuclearRecipeRegistry.containsInput(NuclearChemicals.FISSILE_FUEL_NAME));
        assertFalse(NuclearRecipeRegistry.containsInput("missing_chemical"));
    }

    @Test
    public void processorRunsBothStableConversionsAtomically() {
        ChemicalToChemicalRecipeProcessorCE processor = new ChemicalToChemicalRecipeProcessorCE(
              NuclearRecipeRegistry.CENTRIFUGING, 100, 100);
        MekGasChemicalType waste = new MekGasChemicalType(NuclearChemicals.NuclearWaste, true);
        assertEquals(10, processor.insertInput(new ChemicalStackCE(waste, 10), Action.EXECUTE));
        assertEquals(RecipeProcessResultCE.PROCESSED, processor.processOnce());
        assertEquals(0, processor.getInputStored());
        assertEquals(1, processor.getOutputStored());
        assertEquals(NuclearChemicals.PLUTONIUM_NAME, processor.getOutput().getType().getRegistryName());
    }
}
