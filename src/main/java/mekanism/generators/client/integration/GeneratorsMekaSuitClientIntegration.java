package mekanism.generators.client.integration;

import mekanism.generators.common.integration.GeneratorsMekaSuitIntegration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

/** Client-only registration for the optional Generators MekaSuit units. */
public final class GeneratorsMekaSuitClientIntegration {

    private GeneratorsMekaSuitClientIntegration() {
    }

    public static void registerItemRenders() {
        ModelLoader.setCustomModelResourceLocation(GeneratorsMekaSuitIntegration.SolarRechargingUnit, 0,
              new ModelResourceLocation(GeneratorsMekaSuitIntegration.SolarRechargingUnit.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(GeneratorsMekaSuitIntegration.GeothermalGeneratorUnit, 0,
              new ModelResourceLocation(GeneratorsMekaSuitIntegration.GeothermalGeneratorUnit.getRegistryName(), "inventory"));
    }
}
