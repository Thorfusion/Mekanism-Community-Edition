package mekanism.nuclear.common.content.fission;

import mekanism.generators.common.GeneratorsBlocks;
import mekanism.nuclear.common.NuclearBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

/** Block roles understood by the 1.7.10 fission reactor validator. */
public enum FissionReactorComponent
{
    AIR,
    CASING,
    GLASS,
    PORT,
    LOGIC_ADAPTER,
    FUEL_ASSEMBLY,
    CONTROL_ROD,
    INVALID;

    public boolean isExterior()
    {
        return this == CASING || this == GLASS || this == PORT
              || this == LOGIC_ADAPTER;
    }

    public static FissionReactorComponent fromWorld(IBlockAccess world,
          FissionReactorPosition position)
    {
        return fromBlock(world.getBlock(position.getX(), position.getY(),
              position.getZ()), world.getBlockMetadata(position.getX(),
              position.getY(), position.getZ()));
    }

    public static FissionReactorComponent fromBlock(Block block, int metadata)
    {
        if (block == null || block == Blocks.air)
        {
            return AIR;
        }
        if (block == NuclearBlocks.FissionReactorCasing)
        {
            return CASING;
        }
        if (isGeneratorGlass(block, metadata,
              GeneratorsBlocks.ReactorGlass))
        {
            return GLASS;
        }
        if (block == NuclearBlocks.FissionReactorPort)
        {
            return PORT;
        }
        if (block == NuclearBlocks.FissionReactorLogicAdapter)
        {
            return LOGIC_ADAPTER;
        }
        if (block == NuclearBlocks.FissionFuelAssembly)
        {
            return FUEL_ASSEMBLY;
        }
        if (block == NuclearBlocks.ControlRodAssembly)
        {
            return CONTROL_ROD;
        }
        return INVALID;
    }

    static boolean isGeneratorGlass(Block block, int metadata,
          Block reactorGlass)
    {
        return block == reactorGlass && metadata == 0;
    }
}
