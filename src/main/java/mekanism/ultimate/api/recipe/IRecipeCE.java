package mekanism.ultimate.api.recipe;

import net.minecraft.util.ResourceLocation;

/** Immutable, identified recipe owned by a backport feature module. */
public interface IRecipeCE<INPUT>
{
    ResourceLocation getId();

    boolean matches(INPUT input);
}
