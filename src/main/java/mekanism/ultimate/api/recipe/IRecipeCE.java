package mekanism.ultimate.api.recipe;

import net.minecraft.util.ResourceLocation;

/**
 * Immutable, identified recipe owned by a backport feature module.
 *
 * <p>{@link #matches(Object)} tests input identity and recipe-specific state.
 * Capacity, energy, and output-space checks belong to the processing component
 * so a recipe can still be found while a machine is temporarily unable to run.</p>
 */
public interface IRecipeCE<INPUT> {

    ResourceLocation getId();

    boolean matches(INPUT input);
}
