package mekanism.ultimate.common.block;

import net.minecraft.world.IBlockAccess;

/** Common-side description consumed by the legacy client JSON renderer. */
public interface ILegacyJsonModelBlock
{
    String getLegacyModel(int metadata);

    default String getLegacyModel(IBlockAccess world, int x, int y, int z,
          int metadata)
    {
        return getLegacyModel(metadata);
    }

    default String[] getLegacyModels()
    {
        return new String[] {getLegacyModel(0)};
    }

    int getLegacyModelRotationX(IBlockAccess world, int x, int y, int z,
          int metadata);

    int getLegacyModelRotationY(IBlockAccess world, int x, int y, int z,
          int metadata);

    void setLegacyRenderType(int renderType);
}
